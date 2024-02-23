package ru.oogis.hydra.logging;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LogHandler extends Handler
{
	private static final String DELETE_EVENTS =
			"delete from logging.log_event where event_time < {0}";
	private static final String DELETE_ROWS =
			"delete top {0} from logging.log_event";
	private static final String INSERT_DETAIL =
			"insert into logging.log_detail (logevent_id, details) values(?, ?)";
	private static final String INSERT_EVENT =
			"insert into logging.log_event (id, log_level, event_time, sender, msg, config_id, channel_id) values(?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_CLEAN_CONFIG =
			"select * from logging.clean_config where id=1";
	private static final String SELECT_EVENT_COUNT =
			"select count(*) from logging.log_event";

	private static Logger log = Logger.getLogger("HydraLogHandler");

	// private static final Object PARAM_TIMESTAMP = "$TIMESTAMP";
	// private static final Object PARAM_LEVEL = "$LEVEL";
	// private static final Object PARAM_MESSAGE = "$MESSAGE";
	// private static final String PARAM_MDC = "$MDC[";
	// private static final Object PARAM_PASSTROUGH = "?";
//	private static final String SELECT_ID =
//			"select logging.log_event_id.nextval as id from dual";
	private static final String SELECT_ID =
			"select nextval('logging.log_event_id_seq') as id;";

	private int cleanType;
	private Connection connection;

	// private List<String> parameters = new ArrayList<String>();
	private String datasourceName;
	private long eventCount;
	private long maxRows;
	private long maxTimeout;

	@Override
	public void close() throws SecurityException
	{
		if (connection != null)
		{
			try
			{
				connection.close();
			}
			catch (SQLException p_ex)
			{
				p_ex.printStackTrace();
			}
		}
	}

	@Override
	public void flush()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void publish(LogRecord p_record)
	{
		if (!initialized())
		{
			return;
		}
		try
		{
			insertRecord(p_record);
			removeOutdated();
		}
		catch (SQLException p_ex)
		{
			redirectToLogger(p_record);
		}
	}

	public void setDatasourceName(String p_datasourceName)
	{
		datasourceName = p_datasourceName;
	}

	private void executeSqlUpdate(String p_command) throws SQLException
	{
		PreparedStatement a_statement = null;
		try
		{
			a_statement = connection.prepareStatement(p_command);
			a_statement.executeUpdate();
		}
		finally
		{
			if (a_statement != null)
			{
				a_statement.close();
			}
		}
	}

	private boolean initialized()
	{
		if (connection != null)
		{
			return true;
		}
		try
		{
			setupConnection();
		}
		catch (Exception p_ex)
		{
			p_ex.printStackTrace();
		}
		return false;
	}

	private void insertDetails(Long p_id, Throwable p_ex) throws SQLException
	{
		if (p_id != null && p_ex != null)
		{
			ByteArrayOutputStream a_stream = new ByteArrayOutputStream();
			PrintWriter a_writer = new PrintWriter(a_stream);
			p_ex.printStackTrace(a_writer);
			a_writer.flush();
			String a_error = new String(a_stream.toByteArray());
			a_writer.close();
			PreparedStatement a_statement = null;
			try
			{
				a_statement = connection.prepareStatement(INSERT_DETAIL);
				a_statement.setLong(1, p_id);
				a_statement.setString(2, a_error);
				a_statement.executeUpdate();
			}
			finally
			{
				if (a_statement != null)
				{
					a_statement.close();
				}
			}
		}
	}

	private void insertEvent(Long p_id, LogRecord p_record) throws SQLException
	{
		PreparedStatement a_statement = null;
		try
		{
			a_statement = connection.prepareStatement(INSERT_EVENT);
			a_statement.setLong(1, p_id);
			a_statement.setInt(2, p_record.getLevel().intValue());
			a_statement.setLong(3, p_record.getMillis());
			a_statement.setString(4, p_record.getLoggerName());
			String[] a_parts = parse(p_record.getMessage());
			a_statement.setString(5, a_parts[2]);
			a_statement.setString(6, a_parts[0]);
			a_statement.setString(7, a_parts[1]);
			a_statement.executeUpdate();
		}
		finally
		{
			if (a_statement != null)
			{
				a_statement.close();
			}
		}
	}

	private void insertRecord(LogRecord p_record) throws SQLException
	{
		if (p_record != null)
		{
			Long a_id = requestId();
			insertEvent(a_id, p_record);
			insertDetails(a_id, p_record.getThrown());
		}
	}

	private String[] parse(String p_message)
	{
		String[] a_result = new String[] { "", "", "" };
		if (p_message != null)
		{
			String[] a_parts = p_message.split(Pattern.quote(";"));
			switch (a_parts.length)
			{
				case 1:
					a_result[2] = a_parts[0];
					break;
				case 2:
					a_result[0] = a_parts[0];
					a_result[2] = a_parts[1];
					break;
				case 3:
					a_result[0] = a_parts[0];
					a_result[1] = a_parts[1];
					a_result[2] = a_parts[2];
			}
		}
		return a_result;
	}

	private void redirectToLogger(LogRecord p_record)
	{
		if (p_record != null)
		{
			log.log(p_record.getLevel(), p_record.getMessage());
		}
	}

	private void removeEvents() throws SQLException
	{
		long a_currentTime = System.currentTimeMillis();
		Long a_limitTime = a_currentTime - maxTimeout;
		String a_time = a_limitTime.toString();
		String a_command = MessageFormat.format(DELETE_EVENTS, a_time);
		executeSqlUpdate(a_command);
	}

	private void removeOutdated() throws SQLException
	{
		updateCleanParams();
		switch (cleanType)
		{
			case 1:
				removeRows();
				break;
			case 2:
				removeEvents();
				break;
		}
	}

	private void removeRows() throws SQLException
	{
		updateEventCount();
		if (eventCount > maxRows)
		{
			String a_command =
					MessageFormat
							.format(DELETE_ROWS, Long.toString(eventCount - maxRows));
			executeSqlUpdate(a_command);
		}
	}

	private Long requestId() throws SQLException
	{
		Statement a_statement = null;
		Long a_result = null;
		try
		{
			a_statement = connection.createStatement();
			ResultSet a_resultSet = a_statement.executeQuery(SELECT_ID);
			if (a_resultSet.next())
			{
				a_result = a_resultSet.getLong("id");
			}
		}
		finally
		{
			if (a_statement != null)
			{
				a_statement.close();
			}
		}
		return a_result;
	}

	private void setupConnection() throws NamingException, SQLException
	{
		InitialContext a_context = new InitialContext();
		DataSource a_dataSource = (DataSource) a_context.lookup(datasourceName);
		if (a_dataSource != null)
		{
			connection = a_dataSource.getConnection();
		}
	}

	private void updateCleanParams() throws SQLException
	{
		PreparedStatement a_statement = null;
		try
		{
			a_statement = connection.prepareStatement(SELECT_CLEAN_CONFIG);
			ResultSet a_resultSet = a_statement.executeQuery();
			if (a_resultSet.next())
			{
				cleanType = a_resultSet.getInt("clean_type");
				maxTimeout = a_resultSet.getLong("max_timeout");
				maxRows = a_resultSet.getLong("max_rows");
			}
			else
			{
				cleanType = 0;
			}
		}
		finally
		{
			if (a_statement != null)
			{
				a_statement.close();
			}
		}
	}

	private void updateEventCount() throws SQLException
	{
		PreparedStatement a_statement = null;
		try
		{
			a_statement = connection.prepareStatement(SELECT_EVENT_COUNT);
			ResultSet a_resultSet = a_statement.executeQuery();
			a_resultSet.next();
			eventCount = a_resultSet.getLong(1);
		}
		finally
		{
			if (a_statement != null)
			{
				a_statement.close();
			}
		}
	}

}

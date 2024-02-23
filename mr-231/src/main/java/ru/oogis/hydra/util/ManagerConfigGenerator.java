package ru.oogis.hydra.util;

import ru.oogis.hydra.config.ChannelConfig;
import ru.oogis.hydra.config.ConfigParameter;
import ru.oogis.hydra.config.ManagerConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static ru.oogis.hydra.util.HydraHelper.*;
import static ru.oogis.hydra.util.Messages.getResource;

public final class ManagerConfigGenerator
{
	private static final String UOM = ".uom";
	private String id;
	private File pathToProject;

	public ManagerConfigGenerator(String p_pathToProject, String p_id) throws IOException
	{
		super();
		pathToProject = new File(p_pathToProject);
		checkFolder(pathToProject, false);
		id = p_id;
	}

	/**
	 * @param p_args
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static void main(String[] p_args) throws IOException, JAXBException
	{
		if (p_args == null || p_args.length < 2)
		{
			System.out
					.println("Path to project folder or interaction manager simple class name is missing!!!");
			System.exit(1);
		}
		ManagerConfigGenerator a_generator = new ManagerConfigGenerator(p_args[0], p_args[1]);
		a_generator.generate();
	}

	public void generate() throws IOException, JAXBException
	{
		System.out.println("Generate intaraction configuration: " + id);
		File a_genFolder = new File(pathToProject, "gen/hydra/");
		File a_configFolder = new File(a_genFolder, id);
		File a_routesFolder = new File(a_configFolder, "routes");
		checkFolder(a_routesFolder, true);
		generateConfigTemplate(a_configFolder);
		System.out.println("Generation of intaraction configuration: " + id
				+ " is completed successfully!");
		System.out.println("Generated file: " + a_configFolder.getAbsolutePath()
				+ "/config.xml");
	}

	private ChannelConfig generateChannelTemplate(File p_file) throws IOException
	{
		RouteTemplateHelper a_helper = new RouteTemplateHelper();
		String a_routes = a_helper.loadRoutesFromFile(p_file);
		List<String> a_paramIds = a_helper.getParameterIds(a_routes);
		ChannelConfig a_channelConfig = new ChannelConfig();
		a_channelConfig.setId(p_file.getName().split(Pattern.quote("."))[0]);
		a_channelConfig.setResourceName(a_channelConfig.getId());
		List<ConfigParameter> a_parameters = new ArrayList<ConfigParameter>();
		a_channelConfig.setParameters(a_parameters);
		for (String a_paramId : a_paramIds)
		{
			ConfigParameter a_parameter = ConfigParameter.generateTemplate();
			a_parameter.setId(a_paramId);
			a_parameter.setDisplayName(getResource(a_paramId + DISPLAY_NAME, a_paramId
					+ DISPLAY_NAME));
			a_parameter.setDescription(getResource(a_paramId + DESCRIPTION, a_paramId
					+ DESCRIPTION));
			a_parameter.setUom(getResource(a_paramId + UOM, a_paramId + UOM));
			a_parameters.add(a_parameter);
		}
		return a_channelConfig;
	}

	private void generateConfigTemplate(File p_configFolder) throws JAXBException,
			IOException
	{
		ManagerConfig a_config = new ManagerConfig(id, id + DISPLAY_NAME, id + DESCRIPTION);
		List<ChannelConfig> a_channels = new ArrayList<ChannelConfig>();
		a_config.setChannels(a_channels);
		a_config.getCounter();
		File[] a_routeFiles = new File(p_configFolder, "routes").listFiles();
		for (int i = 0; i < a_routeFiles.length; i++)
		{
			a_channels.add(generateChannelTemplate(a_routeFiles[i]));
		}
		JAXBContext a_context = JAXBContext.newInstance(ManagerConfig.class);
		Marshaller a_marshaller = a_context.createMarshaller();
		a_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		a_marshaller.marshal(a_config, new File(p_configFolder, "config.xml"));
	}

}

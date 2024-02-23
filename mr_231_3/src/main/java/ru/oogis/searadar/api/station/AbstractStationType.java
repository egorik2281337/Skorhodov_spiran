package ru.oogis.searadar.api.station;

import ru.oogis.searadar.api.convert.SearadarExchangeConverter;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStationType implements SearadarStationType{

    private final String typeName;
    private SearadarExchangeConverter converter;
    protected final Map<String, Object> beansForRegistration = new HashMap<>();


    public AbstractStationType(final String typeName) {

        super();
        this.typeName = typeName;
        initialize();

    }


    private void initialize() {
        doInitialize();
    }

    protected void doInitialize() {}

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public SearadarExchangeConverter getConverter() {
        if (converter == null) converter = createConverter();
        return converter;
    }

    @Override
    public Map<String, Object> getBeansForRegistration() {
        return beansForRegistration;
    }

    protected abstract SearadarExchangeConverter createConverter();
}

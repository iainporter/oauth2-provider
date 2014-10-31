package com.porterhead.filter.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iainporter on 31/10/2014.
 */
public class ModifiableRequestParametersWrapper extends HttpServletRequestWrapper
{
    private final Map<String, String[]> modifiableParameters;
    private Map<String, String[]> allParameters = null;

    public ModifiableRequestParametersWrapper(final HttpServletRequest request,
                                              final Map<String, String[]> modifiableParameters)
    {
        super(request);
        this.modifiableParameters = new TreeMap<String, String[]>();
        this.modifiableParameters.putAll(modifiableParameters);
    }

    @Override
    public String getParameter(final String name)
    {
        String[] parameters = getParameterMap().get(name);
        if (parameters != null)
        {
            return parameters[0];
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if(allParameters == null) {
            allParameters = new TreeMap<String, String[]>();
            allParameters.putAll(super.getParameterMap());
            allParameters.putAll(modifiableParameters);
        }
        return Collections.unmodifiableMap(allParameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(final String name){
        return getParameterMap().get(name);
    }
}

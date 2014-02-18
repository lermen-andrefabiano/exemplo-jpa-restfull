package br.com.fwk.spring.logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Log4jConfigurer;

/**
 * 
 * @author rafael.oliveira
 * @since Oct 25, 2010
 */
public class Log4jDirectConfigurer implements InitializingBean
{
    private String location;

    private long refreshInterval;

    public void afterPropertiesSet() throws Exception
    {
        if (location == null)
        {
            return;
        }

        if (refreshInterval == 0)
        {
            Log4jConfigurer.initLogging(location);
        }
        else
        {
            Log4jConfigurer.initLogging(location, refreshInterval);
        }
    }

    // Attribute injectors
    public void setLocation(String location)
    {
        this.location = location;
    }

    public void setRefreshInterval(long refreshInterval)
    {
        this.refreshInterval = refreshInterval;
    }
}

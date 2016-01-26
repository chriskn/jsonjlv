package com.github.chriskn.jsonjlv.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chriskn.jsonjlv.ui.properties.PreferenceManager;
import com.github.chriskn.jsonjlv.Server;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "com.github.chriskn.jsonjlv"; //$NON-NLS-1$
    private static final String LOG4J_PROPERTIES_PATH = "config/log4j.properties";
    private static Activator plugin;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private PreferenceManager preferenceManager;
    private Server server;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        configureLogging();
        preferenceManager = new PreferenceManager(getPreferenceStore());
        if (preferenceManager.isServerAutoStart()) {
            try {
                startServer();
            } catch (Exception e) {
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        stopServer();
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public void startServer() throws Exception {
        if (server != null) {
            stopServer();
        }
        try {
            int portIn = preferenceManager.getIncommingPortNumber();
            int portOut = preferenceManager.getOutgoingPortNumber();
            String ip = preferenceManager.getOutgoingIp();
            server = new Server(portIn, portOut, ip);
            server.start();
            logger.debug("Started Server listening on port " + portIn + " sending to " + ip + ":" + portOut);
        } catch (Exception e) {
            logger.error("Error while starting server. Stopping Server.");
            stopServer();
            throw e;
        }
    }

    public void stopServer() {
        if (server == null) {
            return;
        }
        logger.debug("Shutting down server");
        server.shutdown();
        server = null;
    }

    private void configureLogging() throws IOException {
        Properties props = new Properties();
        URL url = getDefault().getBundle().getEntry(LOG4J_PROPERTIES_PATH);
        try (FileInputStream configFile = new FileInputStream(FileLocator.toFileURL(url).getFile())) {
            props.load(configFile);
        }
        String logFileLocation = plugin.getStateLocation().toString();
        props.put("log.dir", logFileLocation);
        PropertyConfigurator.configure(props);
    }

    @Override
    public void earlyStartup() {
        preferenceManager = new PreferenceManager(getPreferenceStore());
        if (preferenceManager.isServerAutoStart()) {
            try {
                startServer();
            } catch (Exception e) {
            }
        }
    }
}

package com.infusionsoft.gradle.version

import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.AgentProxyException
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import com.jcraft.jsch.agentproxy.USocketFactory
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.util.FS
import org.gradle.api.GradleScriptException

class JgitUtil {

    static void setJgitToUseSshAgent() {
        def sessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                // This can be removed, but the overriden method is required since JschConfigSessionFactory is abstract
                session.setConfig('StrictHostKeyChecking', 'false')
            }
            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                Connector con = null
                try {
                    if (SSHAgentConnector.isConnectorAvailable()) {
                        USocketFactory usf = new JNAUSocketFactory()
                        con = new SSHAgentConnector(usf)
                    }
                } catch (AgentProxyException e) {
                    throw new GradleScriptException(
                            'Something went wrong when testing the connection to the ssh-agent', e)
                }

                if (con == null) {
                    return super.createDefaultJSch(fs)
                }
                final JSch jsch = new JSch()
                jsch.setConfig('PreferredAuthentications', 'publickey')
                IdentityRepository irepo = new RemoteIdentityRepository(con)
                jsch.setIdentityRepository(irepo)
                knownHosts(jsch, fs) // private method from parent class, yeah for Groovy!
                jsch
            }
        }

        SshSessionFactory.setInstance(sessionFactory)
    }
}

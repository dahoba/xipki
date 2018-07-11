/*
 *
 * Copyright (c) 2013 - 2018 Lijun Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xipki.ca.server.mgmt.shell;

import java.io.ByteArrayInputStream;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.completers.FileCompleter;
import org.xipki.ca.server.mgmt.api.CaMgmtException;
import org.xipki.ca.server.mgmt.shell.completer.RequestorNameCompleter;
import org.xipki.security.util.X509Util;
import org.xipki.shell.CmdFailure;
import org.xipki.util.Base64;
import org.xipki.util.IoUtil;

/**
 * TODO.
 * @author Lijun Liao
 * @since 2.0.0
 */

@Command(scope = "ca", name = "requestor-up", description = "update requestor")
@Service
public class RequestorUpdateAction extends CaAction {

  @Option(name = "--name", aliases = "-n", required = true, description = "requestor name")
  @Completion(RequestorNameCompleter.class)
  protected String name;

  @Option(name = "--cert", required = true,
      description = "DER encoded requestor certificate file")
  @Completion(FileCompleter.class)
  protected String certFile;

  @Override
  protected Object execute0() throws Exception {
    // check if the certificate is valid
    byte[] certBytes = IoUtil.read(certFile);
    X509Util.parseCert(new ByteArrayInputStream(certBytes));
    String msg = "CMP requestor " + name;
    try {
      caManager.changeRequestor(name, Base64.encodeToString(certBytes));
      println("updated " + msg);
      return null;
    } catch (CaMgmtException ex) {
      throw new CmdFailure("could not update " + msg + ", error: " + ex.getMessage(), ex);
    }
  }

}

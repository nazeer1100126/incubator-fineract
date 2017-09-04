/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.gcm.domain;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.gcm.GcmConstants;
import org.apache.fineract.infrastructure.gcm.domain.Message.Priority;
import org.apache.fineract.infrastructure.gcm.domain.Message.Builder;


public class Test {

	public void main(String[] args) throws IOException {
		Notification notification = new Notification.Builder("default")
		 .title("Hello world!")
		 .body("Here is a more detailed description")
		 .build();
		 Map<String, String> data= new LinkedHashMap<>();
		 data.put("msg", "b03adf8141ef3ab1");
		 Builder b = new Builder();
		 b.notification(notification);
		 b.dryRun(false);
		 b.contentAvailable(true);
		 b.timeToLive(30);
		 b.priority(Priority.HIGH);
		 b.delayWhileIdle(true);
		 Message msg = b.build();
		 Sender s = new Sender(GcmConstants.SERVER_KEY_ID);
		 Result res = s.send(msg, GcmConstants.SERVER_KEY_ID, 3);
		 //Result res1 = s.send(msg, Constants.regKey1, 3);
		 System.out.println("result of getSuccess  : "+res.getSuccess());
		 System.out.println("result of  getCanonicalRegistrationId : "+res.getCanonicalRegistrationId());
		 System.out.println("result of getMessageId  : "+res.getMessageId());
		 System.out.println("result of getMessageId  : "+res.getFailedRegistrationIds());

	}

}








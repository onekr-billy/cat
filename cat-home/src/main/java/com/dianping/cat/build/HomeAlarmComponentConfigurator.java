/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dianping.cat.build;

import java.util.ArrayList;
import java.util.List;

import com.dianping.cat.alarm.spi.decorator.Decorator;
import com.dianping.cat.alarm.spi.receiver.Contactor;
import com.dianping.cat.config.web.url.UrlPatternConfigManager;
import com.dianping.cat.report.alert.browser.*;
import com.dianping.cat.report.alert.thirdParty.*;
import com.dianping.cat.service.ProjectService;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.alarm.spi.config.AlertConfigManager;
import com.dianping.cat.report.alert.AlarmManager;
import com.dianping.cat.report.alert.config.BaseRuleHelper;
import com.dianping.cat.report.alert.spi.config.UserDefinedRuleManager;

public class HomeAlarmComponentConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {

		List<Component> all = new ArrayList<Component>();

		all.add(A(AlarmManager.class));
		all.add(A(AlertConfigManager.class));
		all.add(A(BaseRuleHelper.class));
		all.add(A(UserDefinedRuleManager.class));

		// third-party
		all.add(C(Decorator.class, ThirdpartyDecorator.ID, ThirdpartyDecorator.class).req(ProjectService.class));
		all.add(C(Contactor.class, ThirdpartyContactor.ID, ThirdpartyContactor.class).req(ProjectService.class,
			AlertConfigManager.class));
		all.add(A(ThirdPartyAlertBuilder.class));
		all.add(A(HttpConnector.class));
		all.add(A(ThirdPartyAlert.class));
		all.add(A(ThirdPartyConfigManager.class));

		// js
		all.add(A(JsRuleConfigManager.class));
		all.add(C(Decorator.class, JsDecorator.ID, JsDecorator.class));
		all.add(C(Contactor.class, JsContactor.ID, JsContactor.class).req(JsRuleConfigManager.class,
			AlertConfigManager.class));
		all.add(A(JsAlert.class));

		// ajax
		all.add(C(Decorator.class, AjaxDecorator.ID, AjaxDecorator.class));
		all.add(C(Contactor.class, AjaxContactor.ID, AjaxContactor.class).req(AlertConfigManager.class,
			ProjectService.class, UrlPatternConfigManager.class));
		all.add(A(AjaxAlert.class));

		return all;
	}
}

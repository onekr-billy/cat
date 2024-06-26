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
package com.dianping.cat.build.report;

import com.dianping.cat.alarm.spi.config.AlertConfigManager;
import com.dianping.cat.alarm.spi.decorator.Decorator;
import com.dianping.cat.alarm.spi.receiver.Contactor;
import com.dianping.cat.config.server.ServerConfigManager;
import com.dianping.cat.consumer.storage.StorageAnalyzer;
import com.dianping.cat.report.alert.storage.cache.StorageCacheAlert;
import com.dianping.cat.report.alert.storage.cache.StorageCacheContactor;
import com.dianping.cat.report.alert.storage.cache.StorageCacheDecorator;
import com.dianping.cat.report.alert.storage.cache.StorageCacheRuleConfigManager;
import com.dianping.cat.report.alert.storage.rpc.StorageRPCAlert;
import com.dianping.cat.report.alert.storage.rpc.StorageRPCContactor;
import com.dianping.cat.report.alert.storage.rpc.StorageRPCDecorator;
import com.dianping.cat.report.alert.storage.rpc.StorageRPCRuleConfigManager;
import com.dianping.cat.report.alert.storage.sql.StorageSQLAlert;
import com.dianping.cat.report.alert.storage.sql.StorageSQLContactor;
import com.dianping.cat.report.alert.storage.sql.StorageSQLDecorator;
import com.dianping.cat.report.alert.storage.sql.StorageSQLRuleConfigManager;
import com.dianping.cat.report.page.storage.config.StorageGroupConfigManager;
import com.dianping.cat.report.page.storage.display.StorageAlertInfoBuilder;
import com.dianping.cat.report.page.storage.service.CompositeStorageService;
import com.dianping.cat.report.page.storage.service.HistoricalStorageService;
import com.dianping.cat.report.page.storage.service.LocalStorageService;
import com.dianping.cat.report.page.storage.task.StorageReportBuilder;
import com.dianping.cat.report.page.storage.task.StorageReportService;
import com.dianping.cat.report.server.RemoteServersManager;
import com.dianping.cat.report.service.ModelService;
import com.dianping.cat.service.ProjectService;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import java.util.ArrayList;
import java.util.List;

public class StorageComponentConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(A(StorageSQLRuleConfigManager.class));
		all.add(A(StorageGroupConfigManager.class));
		all.add(A(StorageCacheRuleConfigManager.class));
		all.add(A(StorageRPCRuleConfigManager.class));

		all.add(A(LocalStorageService.class));
		all.add(C(ModelService.class, "storage-historical", HistoricalStorageService.class) //
								.req(StorageReportService.class, ServerConfigManager.class));
		all.add(C(ModelService.class, StorageAnalyzer.ID, CompositeStorageService.class) //
								.req(ServerConfigManager.class, RemoteServersManager.class) //
								.req(ModelService.class, new String[] { "storage-historical" }, "m_services"));

		all.add(A(StorageReportBuilder.class));

		all.add(A(StorageReportService.class));

		all.add(C(Contactor.class, StorageSQLContactor.ID, StorageSQLContactor.class).req(AlertConfigManager.class));
		all.add(C(Contactor.class, StorageCacheContactor.ID, StorageCacheContactor.class).req(AlertConfigManager.class));
		all.add(C(Contactor.class, StorageRPCContactor.ID, StorageRPCContactor.class)
			.req(AlertConfigManager.class,	ProjectService.class));

		all.add(C(Decorator.class, StorageSQLDecorator.ID, StorageSQLDecorator.class));
		all.add(C(Decorator.class, StorageCacheDecorator.ID, StorageCacheDecorator.class));
		all.add(C(Decorator.class, StorageRPCDecorator.ID, StorageRPCDecorator.class));

		all.add(A(StorageAlertInfoBuilder.class));
		all.add(A(StorageSQLAlert.class));
		all.add(A(StorageCacheAlert.class));
		all.add(A(StorageRPCAlert.class));

		return all;
	}
}

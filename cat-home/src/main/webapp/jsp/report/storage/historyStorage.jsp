<%@ page session="false" language="java" pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx"	type="com.dianping.cat.report.page.storage.Context" scope="request" />
<jsp:useBean id="payload"	type="com.dianping.cat.report.page.storage.Payload" scope="request" />
<jsp:useBean id="model"	type="com.dianping.cat.report.page.storage.Model" scope="request" />
<c:set var="report" value="${model.report}" />

<a:historyStorageReport title="Storage Report"
	navUrlPrefix="type=${payload.type}&id=${payload.id}&operations=${payload.operations}">
	<jsp:attribute name="subtitle">${w:format(payload.historyStartDate,'yyyy-MM-dd HH:mm:ss')} to ${w:format(payload.historyDisplayEndDate,'yyyy-MM-dd HH:mm:ss')}</jsp:attribute>
	<jsp:body>
<table class="machines">
	<tr style="text-align: left">
		<th>&nbsp;[&nbsp; <c:choose>
				<c:when test="${model.ipAddress eq 'All'}">
					<a href="?op=${payload.action.name}&type=${payload.type}&reportType=${payload.reportType}&domain=${model.domain}&id=${payload.id}&date=${model.date}&operations=${payload.operations}"
								class="current">All</a>
				</c:when>
				<c:otherwise>
					<a href="?op=${payload.action.name}&type=${payload.type}&reportType=${payload.reportType}&domain=${model.domain}&id=${payload.id}&date=${model.date}&operations=${payload.operations}">All</a>
				</c:otherwise>
			</c:choose> &nbsp;]&nbsp; <c:forEach var="ip" items="${model.ips}">
   	  		&nbsp;[&nbsp;
   	  		<c:choose>
					<c:when test="${model.ipAddress eq ip}">
						<a href="?op=${payload.action.name}&type=${payload.type}&reportType=${payload.reportType}&domain=${model.domain}&id=${payload.id}&ip=${ip}&date=${model.date}&operations=${payload.operations}"
									class="current">${ip}</a>
					</c:when>
					<c:otherwise>
						<a href="?op=${payload.action.name}&type=${payload.type}&reportType=${payload.reportType}&domain=${model.domain}&id=${payload.id}&ip=${ip}&date=${model.date}&operations=${payload.operations}">${ip}</a>
					</c:otherwise>
				</c:choose>
   	 		&nbsp;]&nbsp;
			 </c:forEach>
		</th>
	</tr>
</table>
<table>
	<tr>
	<td>
		<div>
		<label class="btn btn-info btn-sm">
 			<input type="checkbox" id="operation_All" onclick="clickAll()" unchecked>All</label><c:forEach var="item" items="${model.operations}"><label class="btn btn-info btn-sm"><input type="checkbox" id="operation_${item}" value="${item}" onclick="clickMe()" unchecked>${item}</label></c:forEach>
 		</div>
	</td>
	<td><input class="btn btn-primary btn-sm "
					value="&nbsp;&nbsp;&nbsp;查询&nbsp;&nbsp;&nbsp;" onclick="query()"
					type="submit" /></td>
	</tr>
</table>
<table class="table table-hover table-striped table-condensed table-bordered"  style="width:100%">

	<tr>
		<th rowspan="2" class="center" style="vertical-align:middle">Domain</th>
		<c:forEach var="item" items="${model.currentOperations}">
			<th class="center" colspan="4">${item}</th>
		</c:forEach>
	</tr>
	<tr>
		<c:forEach var="item" items="${model.currentOperations}">
			<th class="right"><a data-rel="tooltip" data-placement="top" title="一分钟内操作总量" href="?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${model.ipAddress}&date=${model.date}&reportType=${payload.reportType}&operations=${payload.operations}&sort=${item};count">个数</a></th>
			<c:if test="${payload.type eq 'SQL'}">
				<th class="right"><a data-rel="tooltip" data-placement="top" title="一分钟内长时间(超过1s)操作总量" href="?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${model.ipAddress}&date=${model.date}&reportType=${payload.reportType}&operations=${payload.operations}&sort=${item};long">Long</a></th>
			</c:if>
			<c:if test="${payload.type eq 'Cache'}">
				<th class="right"><a data-rel="tooltip" data-placement="top" title="一分钟内长时间(超过50ms)操作总量" href="?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${model.ipAddress}&date=${model.date}&reportType=${payload.reportType}&operations=${payload.operations}&sort=${item};long">Long</a></th>
			</c:if>
			<c:if test="${payload.type eq 'RPC'}">
				<th class="right"><a data-rel="tooltip" data-placement="left" title="一段时间内长时间(超过100ms)操作总量" href="?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${model.ipAddress}&date=${model.date}&operations=${payload.operations}&sort=${item};long">Long</a></th>
			</c:if>
			<th class="right"><a data-rel="tooltip" data-placement="top" title="一分钟内操作平均响应时间" href="?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${model.ipAddress}&date=${model.date}&reportType=${payload.reportType}&operations=${payload.operations}&sort=${item};avg">平均耗时</a></th>
			<th class="right"><a data-rel="tooltip" data-placement="top" title="一分钟内错误操作总数" href="?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${model.ipAddress}&date=${model.date}&reportType=${payload.reportType}&operations=${payload.operations}&sort=${item};error">Error</a></th>
		</c:forEach>
	</tr>
	<c:forEach var="domain" items="${model.machine.domains}"
		varStatus="index">
		<tr>
		<td class="center">${domain.key}</td>
		<c:forEach var="item" items="${model.currentOperations}">
			<td class="right">${w:format(domain.value.operations[item].count,'#,###,###,###,##0')}</td>
			<td class="right">${w:format(domain.value.operations[item].longCount,'#,###,###,###,##0')}</td>
			<td class="right">${w:format(domain.value.operations[item].avg,'###,##0.0')}</td>
			<td class="right"><c:choose>
			<c:when test="${domain.value.operations[item].error > 0}">
				<span class="badge badge-danger">${w:format(domain.value.operations[item].error,'#,###,###,###,##0')}</span>
			</c:when>
			<c:otherwise>
				${w:format(domain.value.operations[item].error,'#,###,###,###,##0')}
			</c:otherwise>
			</c:choose></td>
		</c:forEach>
		</tr>
	</c:forEach>
</table>
</jsp:body>
</a:historyStorageReport>

<script type="text/javascript">
	var fs = "${model.currentOperations}";
	fs = fs.replace(/[\[\]]/g,'').split(', ');
	var allfs = '${model.operations}';
	allfs = allfs.replace(/[\[\]]/g,'').split(', ');

	function clickMe() {
		var num = 0;
		for( var i=0; i<allfs.length; i++){
		 	var f = "operation_" + allfs[i];
			if(document.getElementById(f).checked){
				num ++;
			}else{
				document.getElementById("operation_All").checked = false;
				break;
			}
		}
		if(num > 0 && num == allfs.length) {
			document.getElementById("operation_All").checked = true;
		}
	}

	function clickAll(fields) {
		for( var i=0; i<allfs.length; i++){
		 	var f = "operation_" + allfs[i];
		 	if(document.getElementById(f) != undefined) {
				document.getElementById(f).checked = document.getElementById("operation_All").checked;
		 	}
		}
	}

	function query() {
		var url = "";
		if(document.getElementById("operation_All").checked == false && allfs.length > 0) {
			for( var i=0; i<allfs.length; i++){
			 	var f = "operation_" + allfs[i];
				if(document.getElementById(f) != undefined
						&& document.getElementById(f).checked){
					url += allfs[i] + ";";
				}
			}
			url = url.substring(0, url.length-1);
		}else{
			url = "";
		}
		window.location.href = "?op=${payload.action.name}&type=${payload.type}&domain=${model.domain}&id=${payload.id}&ip=${payload.ipAddress}&reportType=${payload.reportType}&date=${model.date}&operations=" + url;
	}

	function init(){
		var num = 0;
		for( var i=0; i<fs.length; i++){
		 	var f = "operation_" + fs[i];
		 	if(document.getElementById(f) != undefined) {
				document.getElementById(f).checked = true;
			}
		}
		if(allfs.length == fs.length){
			document.getElementById("operation_All").checked = true;
		}
	}

	$(document).ready(function() {
		$('[data-rel=tooltip]').tooltip();

		if('${payload.type}' == 'SQL'){
			$('#Database_report').addClass('active open');
			$('#database_operation').addClass('active');
		}else if('${payload.type}' == 'Cache'){
			$('#Cache_report').addClass('active open');
			$('#cache_info').addClass('active');
		}else if('${payload.type}' == 'RPC'){
			$('#RPC_report').addClass('active open');
			$('#rpc_operation').addClass('active');
		}
		init();
	});
</script>

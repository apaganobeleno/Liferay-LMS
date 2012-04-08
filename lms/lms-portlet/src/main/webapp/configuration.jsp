<%--
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
	String titleXml = LocalizationUtil.getLocalizationXmlFromPreferences(preferences, renderRequest, "title");
	String descriptionXml = LocalizationUtil.getLocalizationXmlFromPreferences(preferences, renderRequest, "description");
%>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%= configurationURL %>" method="post" name="fm" cssClass="lmsConfiguration">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	
	<aui:select label="id" name='exam_config_id'>
	
		<!-- TODO: val�di contentek -->
		<aui:option selected='<%= true %>' value="new"><liferay-ui:message key="new" /></aui:option>
		<aui:option selected='<%= false %>' value="1">1</aui:option>
		<aui:option selected='<%= false %>' value="2">2</aui:option>
	</aui:select>
	<%
		String changeSubmitScript = renderResponse.getNamespace() + "setSubmitModeAndSubmit('change-exam');";
	%>
	<aui:button value="change" type="submit" name="change_exam_config" onClick='<%= changeSubmitScript %>'/> 
	<br /> <br />
	
	<liferay-ui:panel collapsible="<%= false %>" extended="<%= true %>" id="exam_fields" persistState="<%= true %>" title="form-fields">
		<aui:fieldset cssClass="rows-container examPages">
			<div class="dummyContainer">
				<aui:input name="pagesetdummy" type="text" value="" />
				<div style="clear:both;"></div>
			</div>
		
			<%
				// TODO: val�di contentek. nem int t�mb, hanem Page t�mb. Lehet, hogy mag�t a paget is �t k�ne adni valahogy a k�v oldalnak.
				// TODO: m�sik �g: nincs page, ekkor 1et az�rt be kell tenni
				int[] formFieldsIndexes = new int[3];
				formFieldsIndexes[0] = 0;
				formFieldsIndexes[1] = 1;
				formFieldsIndexes[2] = 2;

				int index = 1;
				for (int formFieldsIndex : formFieldsIndexes) {
					request.setAttribute("configuration.jsp-pageindex", String.valueOf(index));
					%>
					
					<div class="lfr-form-row" id="<portlet:namespace/>pagefieldset<%=index%>">
						<div class="row-fields">
							<liferay-util:include page="/edit_page.jsp" servletContext="<%= application %>" />
						</div>
					</div>
					
					<%
					index++;
				}
				%>
		</aui:fieldset>
	</liferay-ui:panel>
	
	<aui:button-row>
		<%
			String jsFunct = renderResponse.getNamespace() + "changeScriptBoxVisibility(this.checked);";
		%>
		<aui:input type="checkbox" name="generate_evaluation_logic" label="autogenerate-code" checked="true" onClick="<%= jsFunct %>"/>
		<div id="<portlet:namespace/>evaluation_logic_script" style="display:none;" class="scriptContainer">
			<aui:input type="textarea" name="evaluation_logic_script" label="code" />
		</div>
		<aui:button type="submit" /> 
	</aui:button-row>
</aui:form>



<aui:script use="aui-base,liferay-auto-fields">
	var examPages = A.one('.examPages');

	<liferay-portlet:renderURL portletConfiguration="true" var="editFieldURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.ADD %>" />
	</liferay-portlet:renderURL>
	
	new Liferay.AutoFields(
		{
			contentBox: examPages,
			fieldIndexes: '<portlet:namespace />pageFieldIndexes',
			sortable: true,
			sortableHandle: '.field-label',
			url: '<%= editFieldURL %>'
		}
	).render();
</aui:script>

<aui:script>
	function <portlet:namespace />setSubmitModeAndSubmit(mode) {
		if (!mode) {
			 throw "LMS Config: setSubmitModeAndSubmit, no mode set.";
		}
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = mode;
		submitForm(document.<portlet:namespace />fm, '<%= configurationURL.toString() %>');
	}
	
	function <portlet:namespace />changeScriptBoxVisibility(hide) {
		if (hide) {
			document.getElementById('<portlet:namespace/>evaluation_logic_script').style.display = "none";
		} else {
			document.getElementById('<portlet:namespace/>evaluation_logic_script').style.display = "block";
		}
	}

</aui:script>


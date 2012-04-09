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

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashSet"%>
<%@ include file="/init.jsp" %>

<div class="aui-field-row field-row">

	<% 	
		int parentPageIndex = ParamUtil.getInteger(renderRequest, "index", GetterUtil.getInteger((String)request.getAttribute(ConfigConstants.RA_CONFIGURATION_JSP_PAGEINDEX))); 
		int questionIndex = ParamUtil.getInteger(renderRequest, "index", GetterUtil.getInteger((String)request.getAttribute(ConfigConstants.RA_CONFIGURATION_JSP_QUESTIONINDEX))); 
		int questionIndexParam = GetterUtil.getInteger((String)request.getAttribute(ConfigConstants.RA_CONFIGURATION_JSP_QUESTIONINDEX));

		if (questionIndexParam != 0) {
			questionIndex = questionIndexParam;
		}
		
		if (request.getAttribute(ConfigConstants.RA_PAGE_INDEX) != null && !((String)request.getAttribute(ConfigConstants.RA_PAGE_INDEX)).isEmpty()) {
			parentPageIndex = GetterUtil.getInteger((String)request.getAttribute(ConfigConstants.RA_PAGE_INDEX));
		}
		
		List<String> questionData = (List<String>)request.getAttribute(ConfigConstants.RA_CONFIGURATION_JSP_QUESTIONDATA);
		if (questionData == null) {
			questionData = new ArrayList<String>();
			questionData.add("");
			questionData.add("");
			questionData.add("");
			questionData.add("");
		}
		
		String fieldIdSuffix = "_p" + parentPageIndex + "_q" + questionIndex;
	%>

	<div class="field-title">
		<span class="field-label">Question <%=questionIndex%> of <%= parentPageIndex %></span>
	</div>
	
	<aui:input type="hidden" name='<%= "_field" + questionIndex %>' />
	
	<div id="examQuestion<%=questionIndex%>">
		<aui:fieldset cssClass="rows-container examQuestion">
		
			<div>
				<aui:input label="title" name='<%= ConfigConstants.getQuestionTitleName(parentPageIndex, questionIndex) %>' type="text" value="<%= questionData.get(1) %>" />
				<aui:input label="key" name='<%= "key" + fieldIdSuffix %>' type="text" value="" />
				<div style="clear:both;"></div>
				<aui:select label="type" name='<%= ConfigConstants.getQuestionTypeName(parentPageIndex, questionIndex) %>'>
					<aui:option selected='<%= questionData.get(0).equals("text") %>' value="text"><liferay-ui:message key="text" /></aui:option>
					<aui:option selected='<%= questionData.get(0).equals("checkbox") %>' value="checkbox"><liferay-ui:message key="checkbox" /></aui:option>
					<aui:option selected='<%= questionData.get(0).equals("radio") %>' value="radio"><liferay-ui:message key="radio" /></aui:option>
				</aui:select>
				<aui:input label="answer" name='<%= "answer" + fieldIdSuffix %>' type="text" value="" />
				<aui:input label="point" name='<%= "point" + fieldIdSuffix %>' type="text" value="" />
				<div style="clear: both;"></div>
			</div>
		
			<%
				ExamTest examConfigIds = (ExamTest)request.getAttribute(ConfigConstants.RA_CONFIGURATION_SELECTED_EXAM_TEST);
				
				String[] answerKeys = questionData.get(2).split(",");
				String[] answerTitle = questionData.get(3).split(",");
				
				
				if (!questionData.get(2).isEmpty()) {
					for (int answerIndex = 1; answerIndex <= answerKeys.length; answerIndex++) {
						%>
						<div class="lfr-form-row" id="<portlet:namespace/>answerfieldset<%=answerIndex%>">
							<div class="row-fields">
								<div class="field-title">
									<span class="field-label">Answer <%=answerIndex%> of Question <%=questionIndex%> of <%= parentPageIndex %></span>
								</div>
								<aui:input type="hidden" name='<%= "_field" + answerIndex  %>' />
								<div>
									<aui:input label="title" name='<%= ConfigConstants.getAnswerTitleName(parentPageIndex, questionIndex, answerIndex) %>' type="text" value="<%= answerTitle[answerIndex-1] %>" />
									<aui:input label="key" name='<%= ConfigConstants.getAnswerKeyName(parentPageIndex, questionIndex, answerIndex) %>' type="text" value="<%= answerKeys[answerIndex-1] %>" />
									<div style="clear:both;"></div>
								</div>
							</div>
						</div>
						<%
					}
				} else {
					%>
					<div class="lfr-form-row" id="<portlet:namespace/>answerfieldset<%=1%>">
						<div class="row-fields">
							<div class="field-title">
								<span class="field-label">Answer <%=1%> of Question <%=questionIndex%> of <%= parentPageIndex %></span>
							</div>
							<aui:input type="hidden" name='<%= "_field" + 1  %>' />
							<div>
								<aui:input label="title" name='<%= ConfigConstants.getAnswerTitleName(parentPageIndex, questionIndex, 1) %>' type="text" value="" />
								<aui:input label="key" name='<%= ConfigConstants.getAnswerKeyName(parentPageIndex, questionIndex, 1) %>' type="text" value="" />
								<div style="clear:both;"></div>
							</div>
						</div>
					</div>
					<%
				}
				%> 
		</aui:fieldset>
		<br/>
		<br/>
	</div>
</div>


<aui:script use="aui-base,liferay-auto-fields">
	
	var examQuestion = A.one('#examPage<%=parentPageIndex%> #examQuestion<%=questionIndex%>');
	
	<% String pageIndexString = parentPageIndex + ""; %>
	<% String questionIndexString = questionIndex + ""; %>
	
	<liferay-portlet:renderURL portletConfiguration="true" var="editPageURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
		<portlet:param name="<%= Constants.CMD %>" value="<%= ConfigConstants.CMD_ADD_ANSWER %>" />
		<portlet:param name="<%= ConfigConstants.CP_PAGE_INDEX %>" value="<%=pageIndexString%>" />
		<portlet:param name="<%= ConfigConstants.CP_QUESTION_INDEX %>" value="<%=questionIndexString%>" />
	</liferay-portlet:renderURL>
	
	new Liferay.AutoFields(
		{
			contentBox: examQuestion,
			fieldIndexes: '<portlet:namespace /><%= ConfigConstants.getAnswerFieldIndexName(parentPageIndex, questionIndex) %>',
			sortable: true,
			sortableHandle: '.field-label',
			url: '<%= editPageURL %>'
		}
	).render();
</aui:script>

package hu.advancedweb.lms.portlet;

import hu.advancedweb.lms.evaluation.DefaultExamEvaluatorLogic;
import hu.advancedweb.lms.evaluation.ExamAnswers;
import hu.advancedweb.lms.evaluation.ExamTest;
import hu.advancedweb.lms.evaluation.ExamValidationResult;
import hu.advancedweb.model.ExamConfig;
import hu.advancedweb.service.ExamAnswerLocalServiceUtil;
import hu.advancedweb.service.ExamConfigLocalServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class ExamPortlet extends MVCPortlet {
	
	/**
	 * Submits the current exam page.
	 * If there is a next page, the appropriate permission is given for the user.
	 * 
	 * @param request
	 * @param response
	 */
    public void submitExamPage(ActionRequest request, ActionResponse response) throws Exception {
    	
    	// DEBUG
//		System.out.println("------------------------");
//
//		Enumeration<?> parms = request.getParameterNames ();
//		List<String> a = new ArrayList<String>();
//		
//		String parmname;
//	    String parmval;
//	    String paramvals;
//		while (parms.hasMoreElements ()) {
//	        parmname = (String) parms.nextElement ();
//	        parmval = request.getParameter (parmname);
//	        String[] parmvals = request.getParameterValues("sports");
//	        paramvals = "";
//	        
//	        if (parmvals != null) {
//		        for (String string : parmvals) {
//		        	paramvals = paramvals+ "," + string;
//				}
//		        System.out.println(paramvals + " is a paramvals!!");
//	        }
//	        a.add(parmname + " : " + parmval);
//	    }
//		Collections.sort(a);
//		for (String string : a) {
//			System.out.println(string);
//		}
//		System.out.println("----------------------");
    	
    	/*
    	 * Save user answers.
    	 */
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute("THEME_DISPLAY");
		String portletId = (String)request.getAttribute("PORTLET_ID");
		PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletId);
		long examConfigId = GetterUtil.getLong(preferences.getValue(JspConstants.PREFERENCE_EXAMID, "-1"));
		Map<String,String> answerMap = new HashMap<String, String>();
		
		ExamConfig examConfig = ExamConfigLocalServiceUtil.getExamConfig(examConfigId);
		ExamTest examTest = new ExamTest(examConfig.getQuestions());
		Map<String, List<String>> pageQuestions = examTest.tests.get(getPageNumber(themeDisplay) + "");
		
		for(String question : pageQuestions.keySet()) {
			String answer = ParamUtil.getString(request, question);
			if (answer == null ) {
				break;
			} else {
				answerMap.put(question, answer);
			}
		}
		
		ExamAnswerLocalServiceUtil.appendAnswers(PortalUtil.getCompanyId(request), themeDisplay.getLayout().getGroupId(), themeDisplay.getUser().getUserId(), examConfigId, getPageNumber(themeDisplay) + "", answerMap);
    	
		
		/*
		 * Set user permission for next page.
		 */
		Layout nextLayout = getNextPage(themeDisplay);

		if (nextLayout != null) {
			String roleName = getRoleNameForLayout(nextLayout);

			User user = themeDisplay.getUser();

			Role existing = null;
			try {

				existing = RoleLocalServiceUtil.getRole(themeDisplay.getCompanyId(), roleName);
			} catch (NoSuchRoleException nsre) {
			}
			if (existing == null) {
				// Create role.

				Map<Locale, String> title = new HashMap<Locale, String>();
				title.put(Locale.ENGLISH, roleName);
				
				existing = RoleLocalServiceUtil.addRole(0, themeDisplay.getCompanyId(), roleName, title, title, 1);

				ResourcePermissionLocalServiceUtil.setResourcePermissions(themeDisplay.getCompanyId(), Layout.class.getName(), 4, "" + nextLayout.getPrimaryKey(), existing.getPrimaryKey(), new String[] { "VIEW" });

			}
			UserLocalServiceUtil.addRoleUsers(existing.getPrimaryKey(), new long[] { user.getPrimaryKey() });
        }
    }
    
    public static DefaultExamEvaluatorLogic getGeneratedEvaluationData(String evaluatorJavascript) {
    	if (evaluatorJavascript == null) {
    		return null;
    	} else {
    		return ExamConfigLocalServiceUtil.rereadDefaultEvaluatorLogic(evaluatorJavascript);
    	}
    }
    
    /**
     * Extracts questions for page.
     * @param request
     * @param preferences
     * @return map of question keys and question parameters
     */
    public static Map<String, List<String>> getQuestionData(RenderRequest request, PortletPreferences preferences) {
		long examConfigId = GetterUtil.getLong(preferences.getValue(JspConstants.PREFERENCE_EXAMID, "-1"));
		
		if (examConfigId == -1L) {
			return null;
		}
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			ExamConfig examConfig = ExamConfigLocalServiceUtil.getExamConfig(examConfigId);
			
			ExamTest examTest = new ExamTest(examConfig.getQuestions());
			
			Map<String, List<String>> pageQuestions = examTest.tests.get(getPageNumber(themeDisplay) + "");
			return pageQuestions;
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return null;
	}
    
    /**
     * Extracts user answers for the page.
     * @param request
     * @param preferences
     * @return the current user's answer for the viewed exam
     */
    public static Map<String,String> getAnswerData(HttpServletRequest request, PortletPreferences preferences) {
    	try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			long examConfigId = GetterUtil.getLong(preferences.getValue(JspConstants.PREFERENCE_EXAMID, "-1"));
    	
			ExamAnswers examAnswers = ExamAnswerLocalServiceUtil.getExamAnswers(PortalUtil.getCompanyId(request), themeDisplay.getLayout().getGroupId(), themeDisplay.getUser().getUserId(), examConfigId, getPageNumber(themeDisplay) + "");
			
			if (examAnswers == null) {
				return new HashMap<String, String>();
			} else {
				Map<String,String> pageAnswers = examAnswers.answers.get(getPageNumber(themeDisplay) + "");
				if (pageAnswers == null) {
					return new HashMap<String, String>(); 
				} else {
					return pageAnswers;
				}
			}
    	} catch (SystemException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Returns exam evaluation data based on the user's previous answers.
     * @param request
     * @param preferences
     * @return the evaluation data for the current test and user
     */
    public static ExamValidationResult getEvaluationData(HttpServletRequest request, PortletPreferences preferences ) {
    	try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			long examConfigId = GetterUtil.getLong(preferences.getValue(JspConstants.PREFERENCE_EXAMID, "-1"));
			ExamValidationResult validationData = ExamAnswerLocalServiceUtil.evaluate(PortalUtil.getCompanyId(request), themeDisplay.getLayout().getGroupId(), themeDisplay.getUser().getUserId(), examConfigId);
			return validationData;
    	} catch (SystemException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Decides if the current user can view the next page of the exam.
     * @param request
     * @return true, if the current user filled the actual page, and has the special role for the next page.
     * @throws SystemException
     * @throws PortalException
     */
    public static boolean canUserViewNextPage(RenderRequest request) throws SystemException, PortalException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		long userid = themeDisplay.getUser().getUserId();
		long companyId = themeDisplay.getUser().getCompanyId();
		
		Layout layout = getNextPage(themeDisplay);
		if (layout == null) {
			return false;
		}
		
		String roleName = getRoleNameForLayout(layout);

		try {

			Role existing = RoleLocalServiceUtil.getRole(companyId, roleName);

			for (Role r : RoleLocalServiceUtil.getUserRoles(userid)) {
				if (r.getPrimaryKey() == existing.getPrimaryKey()) {
					return true;
				}
			}
			return false;

		} catch (NoSuchRoleException nsre) {
			return false;
		}
	}
    
    /**
	 * @param themeDisplay
	 * @return the URL for the exam's next page
	 */
	public static String getNextPageUrl(RenderRequest request) throws SystemException, PortalException {
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		Layout nextPage = getNextPage(themeDisplay);
		return (nextPage == null) ? null : nextPage.getFriendlyURL();
	}
	
	/**
	 * @param layout
	 * @return the generated rolename for the given layout
	 */
	private static String getRoleNameForLayout(Layout layout) {
		return "ExamViewFor" + layout.getFriendlyURL().substring(layout.getFriendlyURL().lastIndexOf("/") + 1);
	}
	
	/**
	 * @param themeDisplay
	 * @return the layout for the exam's next page
	 */
	private static Layout getNextPage(ThemeDisplay themeDisplay) throws PortalException, SystemException {
		Layout parent = LayoutLocalServiceUtil.getLayout(themeDisplay.getLayout().getParentPlid());
        int nextPageIndex = parent.getChildren().indexOf(themeDisplay.getLayout()) + 1;
        
        if (nextPageIndex < parent.getChildren().size()) {
			return parent.getChildren().get(nextPageIndex);
		} else {
			return null;
		}
	}
	
	/**
	 * @param themeDisplay
	 * @return the exam page number currently viewed
	 */
	public static int getPageNumber(ThemeDisplay themeDisplay) throws PortalException, SystemException {
		Layout parent = LayoutLocalServiceUtil.getLayout(themeDisplay.getLayout().getParentPlid());
        int nextPageIndex = parent.getChildren().indexOf(themeDisplay.getLayout()) + 1;
        return nextPageIndex;
	}
	
	/**
	 * Decides if the current user already answered the page or not.
	 * @param request
	 * @param preferences
	 * @return true, if the current user filled the page
	 */
	public static boolean isPageAnswered(HttpServletRequest request, PortletPreferences preferences ) {
		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			long examConfigId = GetterUtil.getLong(preferences.getValue(JspConstants.PREFERENCE_EXAMID, "-1"));
			boolean isPageAnswered = ExamAnswerLocalServiceUtil.isPageAnswered(PortalUtil.getCompanyId(request), themeDisplay.getLayout().getGroupId(), themeDisplay.getUser().getUserId(), examConfigId, getPageNumber(themeDisplay) + "");
			return isPageAnswered;
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}

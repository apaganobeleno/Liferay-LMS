package hu.advancedweb.model.impl;

import com.liferay.portal.kernel.exception.SystemException;

import hu.advancedweb.model.ExamAnswer;

import hu.advancedweb.service.ExamAnswerLocalServiceUtil;

/**
 * The extended model base implementation for the ExamAnswer service. Represents a row in the &quot;lms_ExamAnswer&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This class exists only as a container for the default extended model level methods generated by ServiceBuilder. Helper methods and all application logic should be put in {@link ExamAnswerImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ExamAnswerImpl
 * @see hu.advancedweb.model.ExamAnswer
 * @generated
 */
public abstract class ExamAnswerBaseImpl extends ExamAnswerModelImpl
    implements ExamAnswer {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this class directly. All methods that expect a exam answer model instance should use the {@link ExamAnswer} interface instead.
     */
    public void persist() throws SystemException {
        if (this.isNew()) {
            ExamAnswerLocalServiceUtil.addExamAnswer(this);
        } else {
            ExamAnswerLocalServiceUtil.updateExamAnswer(this);
        }
    }
}

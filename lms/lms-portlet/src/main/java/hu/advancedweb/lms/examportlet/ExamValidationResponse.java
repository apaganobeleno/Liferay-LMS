package hu.advancedweb.lms.examportlet;


/**
 * TODO
 * kont�nere a teszt ki�rt�kel�s�nek
 * dbben t�roljuk majd, vagy mindig on the fly az aktu�lis oldal eredm�ny�t adjuk az usernek?
 */
public class ExamValidationResponse {
	private String responseText;

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	
	
}

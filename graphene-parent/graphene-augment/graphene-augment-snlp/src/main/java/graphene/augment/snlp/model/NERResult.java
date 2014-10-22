package graphene.augment.snlp.model;

public class NERResult {
	private String word, answer;

	public NERResult(String word, String answer) {
		super();
		this.word = word;
		this.answer = answer;
	}

	/**
	 * @return the word
	 */
	public final String getWord() {
		return word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public final void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the answer
	 */
	public final String getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public final void setAnswer(String answer) {
		this.answer = answer;
	}
}

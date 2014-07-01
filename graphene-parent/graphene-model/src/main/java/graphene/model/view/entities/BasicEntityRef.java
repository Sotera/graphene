package graphene.model.view.entities;


public class BasicEntityRef {
	private String accountNumber;;

	private String accountType;

	private String customerNumber;

	private String customerType;

	private String dateEnd;

	private String dateStart;

	private Integer entityrefId;

	private String identifier;

	private String identifierColumnSource;

	private String identifierTableSource;

	private Integer idtypeId;

	public BasicEntityRef() {
		// TODO Auto-generated constructor stub
	}

	public BasicEntityRef(String accountNumber, String accountType,
			String customerNumber, String customerType, String dateEnd,
			String dateStart, Integer entityrefId, String identifier,
			String identifierColumnSource, String identifierTableSource,
			Integer idtypeId) {
		super();
		this.accountNumber = accountNumber;
		this.accountType = accountType;
		this.customerNumber = customerNumber;
		this.customerType = customerType;
		this.dateEnd = dateEnd;
		this.dateStart = dateStart;
		this.entityrefId = entityrefId;
		this.identifier = identifier;
		this.identifierColumnSource = identifierColumnSource;
		this.identifierTableSource = identifierTableSource;
		this.idtypeId = idtypeId;
	}

	/**
	 * @return the accountNumber
	 */
	public final String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @return the accountType
	 */
	public final String getAccountType() {
		return accountType;
	}

	/**
	 * @return the customerNumber
	 */
	public final String getCustomerNumber() {
		return customerNumber;
	}

	/**
	 * @return the customerType
	 */
	public final String getCustomerType() {
		return customerType;
	}

	/**
	 * @return the dateEnd
	 */
	public final String getDateEnd() {
		return dateEnd;
	}

	/**
	 * @return the dateStart
	 */
	public final String getDateStart() {
		return dateStart;
	}

	/**
	 * @return the entityrefId
	 */
	public final Integer getEntityrefId() {
		return entityrefId;
	}

	/**
	 * @return the identifier
	 */
	public final String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the identifierColumnSource
	 */
	public final String getIdentifierColumnSource() {
		return identifierColumnSource;
	}

	/**
	 * @return the identifierTableSource
	 */
	public final String getIdentifierTableSource() {
		return identifierTableSource;
	}

	/**
	 * @return the idtypeId
	 */
	public final Integer getIdtypeId() {
		return idtypeId;
	}

	/**
	 * @param accountNumber
	 *            the accountNumber to set
	 */
	public final void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @param accountType
	 *            the accountType to set
	 */
	public final void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @param customerNumber
	 *            the customerNumber to set
	 */
	public final void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	/**
	 * @param customerType
	 *            the customerType to set
	 */
	public final void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	/**
	 * @param dateEnd
	 *            the dateEnd to set
	 */
	public final void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	/**
	 * @param dateStart
	 *            the dateStart to set
	 */
	public final void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	/**
	 * @param entityrefId
	 *            the entityrefId to set
	 */
	public final void setEntityrefId(Integer entityrefId) {
		this.entityrefId = entityrefId;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param identifierColumnSource
	 *            the identifierColumnSource to set
	 */
	public final void setIdentifierColumnSource(String identifierColumnSource) {
		this.identifierColumnSource = identifierColumnSource;
	}

	/**
	 * @param identifierTableSource
	 *            the identifierTableSource to set
	 */
	public final void setIdentifierTableSource(String identifierTableSource) {
		this.identifierTableSource = identifierTableSource;
	}

	/**
	 * @param idtypeId
	 *            the idtypeId to set
	 */
	public final void setIdtypeId(Integer idtypeId) {
		this.idtypeId = idtypeId;
	}
}

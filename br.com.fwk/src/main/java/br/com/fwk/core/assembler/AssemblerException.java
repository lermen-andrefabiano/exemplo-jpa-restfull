package br.com.fwk.core.assembler;

/**
 * exception lançada quando ocoorer erro no parser do assembler
 * @author fabio.arezi
 *
 */
public class AssemblerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AssemblerException(String string) {
		super(string);
	}

	public AssemblerException(Throwable cause) {
		super(cause);
	}

	public AssemblerException(String msg, Throwable cause) {
		super(msg, cause);
	}

}

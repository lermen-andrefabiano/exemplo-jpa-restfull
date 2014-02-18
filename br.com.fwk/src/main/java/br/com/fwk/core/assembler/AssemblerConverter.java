package br.com.fwk.core.assembler;

import java.lang.reflect.ParameterizedType;

public abstract class AssemblerConverter <I, O> {
	private Class<O> outType;
	private Class<I> inType;
	public abstract O convert(I v);

	@SuppressWarnings("unchecked")
	public AssemblerConverter() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.inType = (Class<I>) genericSuperclass.getActualTypeArguments()[0];
		this.outType = (Class<O>) genericSuperclass.getActualTypeArguments()[1];
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inType == null) ? 0 : inType.hashCode());
		result = prime * result + ((outType == null) ? 0 : outType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof AssemblerConverter)) {
			return false;
		}
		AssemblerConverter<?, ?> outro = (AssemblerConverter<?,?>)obj;
			
		return outType == outro.getOutType() && inType == outro.getInType();
	}
	
	

	public Class<O> getOutType(){
		return outType;
	}
	
	public Class<I> getInType(){
		return inType;
	}

}

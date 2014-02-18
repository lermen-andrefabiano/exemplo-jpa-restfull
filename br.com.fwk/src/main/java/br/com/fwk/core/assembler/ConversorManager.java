package br.com.fwk.core.assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ConversorManager {

	private List<AssemblerConverter<?,?>> conversores =  new ArrayList<AssemblerConverter<?,?>>();

	private Map<String, Integer> ixMap =  new HashMap<String, Integer>(); // indice

	public void add(AssemblerConverter<?,?> conv) {
		conversores.add(conv);
		ixMap.put(conv.getInType().getName()+"-"+conv.getOutType().getName(), conversores.size()-1);
	}


	private AssemblerConverter<?, ?> findConvert(Object inObj, Class<?> outType) {
		
		String id = inObj.getClass().getName()+"-"+outType.getName();
		Integer ix = ixMap.get(id);
		if (ix != null) {
			if (ix == -1) {
				return null; // cache do 'nao achou'
			} 
			else {
				return conversores.get(ix);
			}
		} 
		else {
			for (AssemblerConverter<?, ?> conv : conversores) {
				if (conv.getInType().isInstance(inObj) && conv.getOutType() == outType) {
					return conv;
				}
			}
			ixMap.put(id, -1); // se nao achou, lembra a cache q essas classes, nao tem conversor
		}
		
		return null;
	}

	public boolean isConvertable(Object inObj, Class<?> outType) {
		return findConvert(inObj, outType) != null;
	}

	
	public <T> T convert(Object inObj, Class<T> outType) throws AssemblerException {

		@SuppressWarnings("rawtypes")
		AssemblerConverter conv = findConvert(inObj, outType);

		if (conv == null) {
			throw new AssemblerException("Tipo "+outType.getName()+" e "+inObj.getClass().getName()+" nao compativeis.");
		}
		
		@SuppressWarnings("unchecked")
		T v = (T) conv.convert(inObj);
		return v;
		
	}

}

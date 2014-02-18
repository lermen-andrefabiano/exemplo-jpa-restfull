package br.com.fwk.core.assembler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/** 
 * classe que fara mapeamento entre as entidades/beans de sistemas e DTOs
 * 
 * @author fabio.arezi
 *
 */
public class Assembler {

	
	private Class<? extends Annotation> transientAnnotation = FieldTransient.class;
	private Class<? extends Annotation> beanAnnotation = FieldBean.class;
	

	
	public <T> T toDTO(Object beanObj, Class<T> dtoClass) {
		return toDTO(beanObj, dtoClass, beanAnnotation);
	}

	public <T> List<T> toDTO(Collection<?> beanList, Class<T> dtoClass) {
		return toDTO(beanList, dtoClass, beanAnnotation);
	}

	public <T> T toBean(Object dtoObj, Class<T> beanClass) {
		return toBean(dtoObj, beanClass, beanAnnotation);
	}

	public <T> List<T> toBean(Collection<?> dtoList, Class<T> beanClass) {
		return toBean(dtoList, beanClass, beanAnnotation);
	}


	private ConversorManager conversorManager = new ConversorManager();
	
	
	public Assembler() {


		addConverter(new AssemblerConverter<Integer, Long>() {
			public Long convert(Integer v) {
				return v.longValue();
			}
		});
		addConverter(new AssemblerConverter<Long, Integer>() {
			public Integer convert(Long v) {
				return v.intValue();
			}
		});

		
		addConverter(new AssemblerConverter<Float, Double>() {
			public Double convert(Float v) {
				return v.doubleValue();
			}
		});
		addConverter(new AssemblerConverter<Double, Float>() {
			public Float convert(Double v) {
				return v.floatValue();
			}
		});

		
		addConverter(new AssemblerConverter<java.util.Date, Calendar>() {
			public Calendar convert(java.util.Date v) {
				Calendar r = Calendar.getInstance();
				r.setTime(v);
				return r;
			}
		});
		addConverter(new AssemblerConverter<Calendar, java.util.Date>() {
			public java.util.Date convert(Calendar v) {
				return v.getTime();
			}
		});

		
		addConverter(new AssemblerConverter<java.util.Date, java.sql.Date>() {
			public java.sql.Date convert(java.util.Date v) {
				return new java.sql.Date(v.getTime());
			}
		});
		addConverter(new AssemblerConverter<java.sql.Date, java.util.Date>() {
			public java.util.Date convert(java.sql.Date v) {
				return new java.util.Date(v.getTime());
			}
		});

		
		
	}


	public void addConverter(AssemblerConverter<?,?> conv) {
		conversorManager.add(conv);
	}

	

	
	public void setTransientAnnotation(Class<? extends Annotation> transientAnnotation) {
		this.transientAnnotation = transientAnnotation;
	}
	
	public void setBeanAnnotation(Class<? extends Annotation> beanAnnotation) {
		this.beanAnnotation = beanAnnotation;
	}
	
	// fazer JAVADOC
	@Deprecated
	protected <T> List<T> toDTO(Collection<?> beanList, Class<T> dtoClass, Class<?> annTypeField) throws AssemblerException {
		if (beanList == null) {
			return null;
		}
		// TODO - ver se a collections for Set, instanciar Hashset e retornar collection
		List<T> dtoList = new ArrayList<T>();

		for (Object beanObj : beanList) {
			dtoList.add( toDTO(beanObj, dtoClass, annTypeField) );
		}
		
		return dtoList;
	}

	

	// fazer JAVADOC
	@Deprecated
	protected <T> T toDTO(Object beanObj, Class<T> dtoClass, Class<?> annTypeField) throws AssemblerException {
		T dtoObj = null;
		try {

			if (beanObj == null) {
				return null;
			}
			else if (dtoClass.isInstance(beanObj) ) {
				@SuppressWarnings("unchecked")
				T t = (T)beanObj;
				return t;
			}
			else if (conversorManager.isConvertable(beanObj, dtoClass) ) {
				return conversorManager.convert(beanObj, dtoClass);
			}
				

			// instancia o dto
			dtoObj = dtoClass.getDeclaredConstructor(new Class[]{}).newInstance(); // ou: (T)Class.forName(annValue)

			// pegar os atributos da dtoClass fererente ao bean 
			for (Field fieldDto : dtoObj.getClass().getDeclaredFields()) {

				if (fieldDto.isAnnotationPresent(transientAnnotation )) {
					continue;
				}
				
				fieldDto.setAccessible(true);

				String fieldNameBean = this.getBeanFieldName(fieldDto, annTypeField);

				String preMethodNameBean = Character.toUpperCase(fieldNameBean.charAt(0))+fieldNameBean.substring(1);
				
				Method methodBean = null;
				try {
					methodBean = beanObj.getClass().getDeclaredMethod("get"+preMethodNameBean);
				} catch (NoSuchMethodException e) {
					try {
						methodBean = beanObj.getClass().getDeclaredMethod("is"+preMethodNameBean);
					} catch (NoSuchMethodException e2) {
						throw new AssemblerException("Metodo '"+"is/get"+preMethodNameBean+"()' nao encontrado no bean "+beanObj.getClass().getSimpleName());
					}
				}

				Object fieldValue = methodBean.invoke(beanObj);

				
				if (fieldValue == null) {
					fieldDto.set(dtoObj, null);
					continue;
				}
				else if (fieldValue instanceof Collection) {
					Collection<?> beanFieldList = (Collection<?>)fieldValue;
					Class<?> genericClass = this.getGenericClass(fieldDto);
					fieldValue = this.toDTO(beanFieldList, genericClass, annTypeField);

				} 
				else if (conversorManager.isConvertable(fieldValue, fieldDto.getType())) { // objeto de valor (String, Integer etc..)
					try {
						fieldValue = conversorManager.convert(fieldValue, fieldDto.getType());
					} catch (AssemblerException e) {
						throw new AssemblerException("Campos '"+fieldDto.getName()+"' e '"+fieldNameBean+"' incompativeis." , e);
					}

				}
				else if (fieldDto.getType().isInstance(fieldValue) ) {
					fieldDto.set(dtoObj, fieldValue);
					continue;
				}
				else if (fieldValue.getClass().isEnum()) {
					Enum<?> enumValue = (Enum<?>) fieldValue;
					if (fieldDto.getType() == String.class) {
						fieldValue = enumValue.name();
					} else if (fieldDto.getType().isEnum()) {
						Method m = fieldDto.getType().getMethod("valueOf", String.class);
						fieldValue = m.invoke(null, enumValue.name());
					} else {
						throw new AssemblerException("Tipo não conhecido para conversao do enum "+fieldValue.getClass().getName());
					}
				}
				else if (fieldDto.getType() == Object[].class) { // array de objects 
					// nao faz nada, só atribui o valor :)
				} 
				else { // objeto de associação (bean ou DTO)

					fieldValue = this.toDTO(fieldValue, fieldDto.getType(), annTypeField);

				}

				fieldDto.set(dtoObj, fieldValue);
			}
			
		} catch (AssemblerException e) {
			throw e;
		} catch (Exception e) {
			throw new AssemblerException(e);
		}
	
		return dtoObj;
	}

	
	
	
	// fazer JAVADOC
	@Deprecated
	protected <T> List<T> toBean(Collection<?> dtoList, Class<T> beanClass, Class<?> annTypeField) throws AssemblerException {
		List<T> beanList = new ArrayList<T>();

		if (dtoList == null) {
			return null;
		}
		
		for (Object dtoObj : dtoList) {
			beanList.add( toBean(dtoObj, beanClass, annTypeField) );
		}
		
		return beanList;
	}

	
	// fazer JAVADOC
	@Deprecated
	protected <T> T toBean(Object dtoObj, Class<T> beanClass, Class<?> annTypeField) throws AssemblerException {
		T beanObj = null;
		try {

			if (dtoObj == null) {
				return null;
			}
			else if (beanClass.isInstance(dtoObj) ) {
				@SuppressWarnings("unchecked")
				T t = (T)dtoObj;
				return t;
			}
			else if (conversorManager.isConvertable(dtoObj, beanClass) ) {
				return conversorManager.convert(dtoObj, beanClass);
			}

			// instancia o bean
			beanObj = beanClass.getDeclaredConstructor(new Class[]{}).newInstance(); // ou: (T)Class.forName(annValue)

			// pegar os atributos da dtoClass fererente ao bean 
			for (Field fieldDto : dtoObj.getClass().getDeclaredFields()) {
				if (fieldDto.isAnnotationPresent(transientAnnotation)) {
					continue;
				}

				String fieldNameBean = this.getBeanFieldName(fieldDto, annTypeField);

				Field fieldBean = beanClass.getDeclaredField(fieldNameBean);
				if (fieldBean == null) {
					throw new AssemblerException("Atributo '"+fieldNameBean+"' nao encontrado no bean");
				}
				fieldBean.setAccessible(true);

				fieldDto.setAccessible(true);
				Object fieldValue = fieldDto.get(dtoObj);

				if (fieldValue == null) {
					fieldBean.set(beanObj, null);
					continue;
				}
				else if (fieldValue instanceof Collection) {
					Collection<?> listFieldValue = (Collection<?>)fieldValue;
					Class<?> genericClass = this.getGenericClass(fieldBean);
					fieldValue = this.toBean(listFieldValue, genericClass, annTypeField); // e chamar toBean pra collection

				} 
				else if (conversorManager.isConvertable(fieldValue, fieldBean.getType())) { // objeto de valor 
					try {
						fieldValue = conversorManager.convert(fieldValue, fieldBean.getType());
					} catch (AssemblerException e) {
						throw new AssemblerException("Campos '"+fieldDto.getName()+"' e '"+fieldBean.getName()+"' incompativeis." , e);
					}
				}
				else if (fieldBean.getType().isInstance(fieldValue) ) {
					fieldBean.set(beanObj, fieldValue);
					continue;
				}
				else if (fieldBean.getType().isEnum()) {
					if (fieldDto.getType() == String.class) {
						Method m = fieldBean.getType().getMethod("valueOf", String.class);
						fieldValue = m.invoke(null, fieldValue);
					}
					else if (fieldDto.getType().isEnum()) {
						Method m = fieldBean.getType().getMethod("valueOf", String.class);
						fieldValue = m.invoke(null, ((Enum<?>)fieldValue).name());
					}
					else {
						throw new AssemblerException("Tipo '"+fieldDto.getType().getName()+"' não suportado para enum '"+fieldBean.getType().getName()+"'");
					}
				}
				else if (fieldBean.getType() == Object[].class) { // array de objects 
					throw new AssemblerException("Arrays de valores não suportado ainda (campo '"+fieldBean.getName()+"')");
				} else { // objeto de associacao (bean ou DTO)
					fieldValue = this.toBean(fieldValue, fieldBean.getType(), annTypeField);
				
				}
				
				fieldBean.set(beanObj, fieldValue);
			}
			
		} catch (AssemblerException e) {
			throw e;
		} catch (Exception e) {
			throw new AssemblerException(e);
		}
	
		return beanObj;
	}

	


	// fazer JAVADOC
	private String getBeanFieldName(Field fieldDto, Class<?> annTypeField) throws AssemblerException {
		String fieldNameBean;

		@SuppressWarnings("unchecked")
		Annotation annField = fieldDto.getAnnotation((Class<Annotation>)annTypeField);
		
		if (annField != null) {
			try {
				fieldNameBean = (String)annTypeField.getMethod("value").invoke(annField);
			} catch (Exception e) {
				throw new AssemblerException(e);
			}
		}
		else {
			fieldNameBean = fieldDto.getName();
		}
		
		return fieldNameBean;
	}



	// fazer JAVADOC
	private Class<?> getGenericClass(Field fieldDto) throws AssemblerException {
//		if (fieldDto.getGenericType() == Object[].class) {
//			return Object[].class;
//		}

		Type[] types = ((ParameterizedType)fieldDto.getGenericType()).getActualTypeArguments();

		if (types.length == 1) {
			if (types[0] instanceof GenericArrayType) {
				return Object[].class;
			}

			if (! (types[0] instanceof Class) ) {
				throw new AssemblerException("Em '"+fieldDto.getDeclaringClass().getSimpleName()+"', no atributo '"+fieldDto.getName()+"', não foi declarado generic corretamente.");
			}
			return (Class<?>)types[0];
		} else {
			throw new AssemblerException("Em '"+fieldDto.getDeclaringClass().getSimpleName()+"', no atributo '"+fieldDto.getName()+"', é esperado declaração de generic!");
		}
	}

	
}

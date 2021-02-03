/**
 * 
 */
package de.kjEngine.io.serilization;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author konst
 *
 */
public class Serializer {

	public static void deserialize(JSONObject obj, Object target) {
		for (Field field : target.getClass().getFields()) {
			if (field.getAnnotation(Serialize.class) != null) {
				String name = field.getName();
				Class<?> type = field.getType();
				if (obj.has(name)) {
					try {
						switch (type.getName().trim()) {
						case "boolean":
							field.setBoolean(target, obj.getBoolean(name));
							break;
						case "byte":
							field.setByte(target, (byte) obj.getInt(name));
							break;
						case "char":
							field.setChar(target, obj.getString(name).charAt(0));
							break;
						case "short":
							field.setShort(target, (short) obj.getInt(name));
							break;
						case "int":
							field.setInt(target, obj.getInt(name));
							break;
						case "float":
							field.setFloat(target, obj.getFloat(name));
							break;
						case "double":
							field.setDouble(target, obj.getDouble(name));
							break;
						case "long":
							field.setLong(target, obj.getLong(name));
							break;
						case "java.lang.String":
							field.set(target, obj.getString(name));
							break;
						default:
							try {
								Class<?> c = Class.forName(type.getName());
								Constructor<?> constructor = null;
								try {
									constructor = c.getConstructor();
								} catch (NoSuchMethodException e) {
								}
								if (constructor == null) {
									continue;
								}
								Object o = constructor.newInstance();
								deserialize(obj.getJSONObject(name), o);
								field.set(target, o);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							}
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (target instanceof Serializable) {
			((Serializable) target).deserialize(obj);
		}
	}

	public static JSONObject serialize(Object obj) {
		JSONObject o = new JSONObject();
		for (Field field : obj.getClass().getFields()) {
			if (field.getAnnotation(Serialize.class) != null) {
				String name = field.getName();
				Class<?> type = field.getType();
				try {
					switch (type.getName().trim()) {
					case "boolean":
						o.put(name, field.getBoolean(obj));
						break;
					case "byte":
						o.put(name, field.getInt(obj));
						break;
					case "char":
						o.put(name, field.get(obj).toString().charAt(0));
						break;
					case "short":
						o.put(name, field.getInt(obj));
						break;
					case "int":
						o.put(name, field.getInt(obj));
						break;
					case "float":
						o.put(name, field.getFloat(obj));
						break;
					case "double":
						o.put(name, field.getDouble(obj));
						break;
					case "long":
						o.put(name, field.getLong(obj));
						break;
					case "java.lang.String":
						o.put(name, field.get(obj).toString());
						break;
					default:
						o.put(name, serialize(field.get(obj)).toMap());
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if (obj instanceof Serializable) {
			JSONObject object = ((Serializable) obj).serialize();
			if (object != null) {
				for (String key : object.keySet()) {
					o.put(key, object.get(key));
				}
			}
		}
		return o;
	}
}

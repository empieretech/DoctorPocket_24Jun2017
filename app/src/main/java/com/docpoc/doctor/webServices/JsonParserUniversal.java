package com.docpoc.doctor.webServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonParserUniversal {


    public Object parseJson(JSONObject data, Object o) {
        try {

            if (data != null) {
                Iterator<String> it = data.keys();

                while (it.hasNext()) {
                    String mKey = it.next();
                    Class c;
                    try {
                        c = Class.forName(o.getClass().getCanonicalName(), false, o.getClass().getClassLoader());

                        Field[] fields = c.getFields();
                        if (data.get(mKey) instanceof Integer) {
                            for (Field filed : fields) {
                                if (filed.getName().equalsIgnoreCase(mKey)) {

                                    if (data.get(mKey) != null) {
                                        filed.set(o, data.getInt(mKey));
                                    } else {
                                        filed.set(o, 0);
                                    }
                                }
                            }

                        } else if (data.get(mKey) instanceof String) {
                            for (Field filed : fields) {
                                if (filed.getName().equalsIgnoreCase(mKey)) {
                                    try {
                                        filed.set(o, data.getString(mKey));
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        } else if (data.get(mKey) instanceof Boolean) {
                            for (Field filed : fields) {
                                if (filed.getName().equalsIgnoreCase(mKey)) {
                                    filed.set(o, data.getBoolean(mKey));
                                }
                            }
                        } else if (data.get(mKey) instanceof JSONArray) {
                            JSONArray array = data.getJSONArray(mKey);
                            if (array.length() > 0) {
                                if (array.get(0) instanceof Integer) {
                                    ArrayList<Integer> intArray = new ArrayList<>();
                                    for (int k = 0; k < array.length(); k++) {
                                        intArray.add(array.getInt(0));
                                    }

                                    for (Field filed : fields) {
                                        if (filed.getName().equalsIgnoreCase(mKey)) {
                                            filed.set(o, intArray);
                                        }
                                    }
                                }
                            }
                           /*

                            String classForName = "com.markteq.wms." + mKey + ".class";
                            Log.i("classForName", classForName);
                            Array array = (Array) Array.newInstance(
                                    Class.forName(classForName), size);
                            for (int i = 0; i < size; i++) {
                                array.set(array, i, parseJson(arry.getJSONObject(i),
                                        array.get(array, i).getClass()
                                                .getName()));
                            }
                            for (Field filed : fields) {
                                if (filed.getName().equalsIgnoreCase(mKey)) {
                                    filed.set(this, array);
                                }
                            }
                            return array;*/
                        } else if (data.get(mKey) instanceof JSONObject) {
                            for (Field filed : fields) {
                                if (filed.getName().equalsIgnoreCase(mKey)) {
                                    filed.set(o, parseJson(data.getJSONObject(mKey), mKey));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

                }
            }
        } catch (Throwable e) {

            e.printStackTrace();
            return null;
        }
        return o;
    }
/*
    public void getJsonFromArray(JSONArray jArray) {
        try {
            for (int i = 0; i < jArray.length(); i++) {

                Object obj = jArray.get(i);

                if (obj instanceof JSONArray) {

                    getJsonFromArray(jArray.getJSONArray((i)));

                } else if (obj instanceof JSONObject) {
                    JSONObject data = jArray.getJSONObject(i);
                    Iterator<String> it = data.keys();
                } else if (obj instanceof Integer) {

                } else if (obj instanceof String) {

                } else if (obj instanceof Boolean) {

                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/

}
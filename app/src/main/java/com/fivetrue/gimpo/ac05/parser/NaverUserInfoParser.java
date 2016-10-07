package com.fivetrue.gimpo.ac05.parser;

import android.util.Log;

import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class NaverUserInfoParser {

    private static final String TAG  = "NaverUserInfoParser";

    private static final String ELEMENT_RESULT_TAG = "result";
    private static final String ELEMENT_RESULT_CODE_TAG = "resultcode";
    private static final String ELEMENT_MESSAGE_TAG = "message";

    private static final String ELEMENT_RESPONSE_TAG = "response";

    private static final String ELEMENT_EMAIL_TAG = "email";
    private static final String ELEMENT_NICKNAME_TAG = "nickname";
    private static final String ELEMENT_ENC_ID_TAG = "enc_id";
    private static final String ELEMENT_PROFILE_IMAGE_TAG = "profile_image";
    private static final String ELEMENT_AGE_TAG = "age";
    private static final String ELEMENT_GENDER_TAG = "gender";
    private static final String ELEMENT_ID_TAG = "id";
    private static final String ELEMENT_NAME_TAG = "name";
    private static final String ELEMENT_BIRTHDAY_TAG = "birthday";
/*
 <?xml version="1.0" encoding="UTF-8" ?><data><result><resultcode>00</resultcode><>success</message></result><response><email><![CDATA[dudrpdjwls@naver.com]]></email><nickname><![CDATA[고구마감자]]></nickname><enc_id><![CDATA[6cea3f02bbbed6a2142142ca77fd3a457c66e0019007de3fbedb5e1fa1522e6d]]></enc_id><profile_image><![CDATA[https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif]]></profile_image><age><![CDATA[30-39]]></age><gender>M</gender><id><![CDATA[15262827]]></id><name><![CDATA[권오진]]></name><birthday><![CDATA[11-01]]></birthday></response></data>
 */
    public static UserInfo parse(String xml){
        UserInfo info = null;
        if(xml != null){
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setCoalescing(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is);

                // get the root element.....
                Element docElement = doc.getDocumentElement();
                // now get the NodeList of root elements
                NodeList resultList = docElement.getElementsByTagName(ELEMENT_RESULT_TAG);
                if(resultList != null && resultList.getLength() > 0){
                    info = new UserInfo();
                    Element result = (Element) resultList.item(0);
                    info.setResultCode(getElementValue(result, ELEMENT_RESULT_CODE_TAG));
                    info.setMessage(getElementValue(result, ELEMENT_MESSAGE_TAG));
                    NodeList responseList = docElement.getElementsByTagName(ELEMENT_RESPONSE_TAG);

                    if(responseList != null && responseList.getLength() > 0){
                        Element response = (Element) responseList.item(0);
                        Log.i(TAG, response.getTagName());
                        info.setEmail(getElementValue(response, ELEMENT_EMAIL_TAG));
                        info.setNickname(getElementValue(response, ELEMENT_NICKNAME_TAG));
                        info.setEncId(getElementValue(response, ELEMENT_ENC_ID_TAG));
                        info.setProfileImage(getElementValue(response, ELEMENT_PROFILE_IMAGE_TAG));
                        info.setAge(getElementValue(response, ELEMENT_AGE_TAG));
                        info.setGender(getElementValue(response, ELEMENT_GENDER_TAG));
                        info.setId(getElementValue(response, ELEMENT_ID_TAG));
                        info.setName(getElementValue(response, ELEMENT_NAME_TAG));
                        info.setBirthday(getElementValue(response, ELEMENT_BIRTHDAY_TAG));
                    }
                }
            }catch (ParserConfigurationException e) {
                Log.w(TAG, "XML parse Error:", e);
                return null;
            } catch (SAXException e) {
                Log.w(TAG, "Wrong XML File Structure", e);
                return null;
            } catch (IOException e) {
                Log.w(TAG, "IOException", e);
                return null;
            }

        }
        return info;
    }

    private static String getElementValue(Element root, String tag){
        String value = null;

        if(root != null && tag != null){
            NodeList valueNode = root.getElementsByTagName(tag);
            if(valueNode != null && valueNode.getLength() > 0){
                Element elementValue = (Element) valueNode.item(0);
                value = elementValue.getFirstChild().getNodeValue();
                Log.i(TAG, tag + " = " + value);
            }
        }

        return value;
    }

}

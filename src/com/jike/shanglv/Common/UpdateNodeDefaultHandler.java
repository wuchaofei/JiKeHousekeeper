package com.jike.shanglv.Common;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jike.shanglv.Update.UpdateNode;

public class UpdateNodeDefaultHandler extends DefaultHandler {
		private List<UpdateNode> nodes;  
	    private UpdateNode node; // 记录当前node  
	    private String tag;    
	  
	    /** 
	     * 重写父类的开始文档方法。用于初始化 
	     */  
	    @Override  
	    public void startDocument() throws SAXException {  
	        nodes = new ArrayList<UpdateNode>();  
	    }  
	  
	    @Override  
	    public void startElement(String uri, String localName, String qName,  
	            Attributes attributes) throws SAXException {  
	        if ("node".equals(localName)) {  
	            String name = attributes.getValue(0); // 取id  
	            node = new UpdateNode();  
	            node.setName(name);
	        }  
	        tag = localName;  
	    }  
	  
	    /** 
	     * 参数： ch 整个XML字符串 start 节点值在整个XML字符串中的索引位置 length 节点值的长度 
	     */  
	    @Override  
	    public void characters(char[] ch, int start, int length)  
	            throws SAXException {  
	        if (tag != null) {  
	            String data = new String(ch, start, length);  
	            if ("version".equals(tag)) {  
	                node.setVersion(data);  
	            } else if ("content".equals(tag)) {  
	                node.setContent(data);  
	            }else if ("download_url".equals(tag)) {  
	                node.setDownload_url(data);  
	            }else if ("updatetime".equals(tag)) {  
	                node.setContent(data);  
	            }else if ("hotelcity".equals(tag)) {  
	                node.setHotelcity(Integer.valueOf(data));  
	            }else if ("flightcity".equals(tag)) {  
	                node.setFlightcity(Integer.valueOf(data));  
	            }else if ("iflightcity".equals(tag)) {  
	                node.setIflightcity(Integer.valueOf(data));  
	            }else if ("traincity".equals(tag)) {  
	                node.setTraincity(Integer.valueOf(data));  
	            }else if ("versionCode".equals(tag)) {  
	                node.setVersionCode(Integer.valueOf(data));  
	            }    
	        }  
	    }  
	  
	    @Override  
	    public void endElement(String uri, String localName, String qName)  
	            throws SAXException {  
	        if ("node".equals(localName)) {  
	            nodes.add(node);  
	            node = null;  
	        }  
	        tag = null;  
	    }  
	  
	    public List<UpdateNode> getUpdateNodes() {  
	        return nodes;  
	    }  
}

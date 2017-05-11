package Portal;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.idw.storage.connector.Archimate3Parser;
import org.idw.storage.connector.GenericParser;
import org.idw.storage.connector.ParserFactory;



/**
 * Servlet implementation class BaseContact
 */
@WebServlet("/archimate/*")
public class BaseContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = Logger.getLogger(BaseContact.class.getName());

	final static String OP_QUERY = "/query";
	final static String OP_PULL = "/pull";
	final static String OP_PUSH = "/push";
	final static String OP_COMMIT = "/commit";

	//public static MongoDBAccess mongo = null;
	public ParserFactory pf = new ParserFactory();

	/**
	 * Default constructor. 
	 */
	public BaseContact() {
		registerParser("archimate3", new Archimate3Parser());
		//u.registerParser("bpmn", new BPMNParser());
		//registerParser("disco", new DiscoResultParser());

	}

	private boolean parseXmlString(String xml){
		return pf.processXmlString(xml);
	}

	private boolean parseJsonString(String json){
		return pf.processJsonString(json);
	}

	public String deriveXmlString(String parserName, Date date) {
		return pf.deriveXmlString(parserName, date);
	}

	public String deriveJsonString(String parserName, Date date) {
		return pf.deriveJsonString(parserName, date);
	}

	private void registerParser(String parserName, GenericParser gp) {
		pf.registerParser(parserName, gp);
	}

	protected String handleRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		String ret ="";
		String path = request.getPathInfo();
		LOGGER.warning("path: "+path);
		if(path.equals(OP_COMMIT)){
			String str = request.getParameter("xml");
			if(str!=null && !str.isEmpty()){
				//String tag = request.getParameter("tag");
				LOGGER.warning("xml: "+str);
				ret = ret+"received document for commit of length "+str.length();
				boolean r = parseXmlString(str);
				LOGGER.warning("result of uploading the data "+r);
			} else {
				str = request.getParameter("json");
				if(str!=null && !str.isEmpty()){
					//String tag = request.getParameter("tag");
					LOGGER.warning("json: "+str);
					ret = ret+"received document for commit of length "+str.length();
					boolean r = parseJsonString(str);
					LOGGER.warning("result of uploading the data "+r);
				} else LOGGER.severe("no valid input found");
			}
		} else
			if (path.equals(OP_QUERY)){
				//String str = request.getParameter("query");
				String modelType = request.getParameter("model-type");
				String outputFormat = request.getParameter("output-format");
				//			String date = request.getParameter("date");
				//			LOGGER.warning("model type: "+modelType+"     date: "+date);
				LOGGER.warning("model type: "+modelType + "    output format:"+ outputFormat);
				if(outputFormat!=null && outputFormat.equals("json")){
					ret = ret+deriveJsonString(modelType, new Date(System.currentTimeMillis()));
				} else if(outputFormat!=null && outputFormat.equals("xml")){
					ret = ret+deriveXmlString(modelType, new Date(System.currentTimeMillis()));
				} else LOGGER.severe("invalid output format parameter");
			}
		return ret;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.warning("started doGet");
		String path = request.getPathInfo();
		response.getWriter().append("Served at: ").append(request.getContextPath()+"with path: "+path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.warning("started doPost");
		String ret =  handleRequest(request,response);
		LOGGER.warning("result: "+ret);
		response.getWriter().append(ret);
	}


}

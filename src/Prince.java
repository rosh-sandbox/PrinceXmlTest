// Copyright (C) 2005-2006, 2010, 2012, 2014 YesLogic Pty. Ltd.
// All rights reserved.

package com.princexml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * The main Prince class.
 */
public class Prince
{
    private PrinceEvents mEvents;
    private String mExePath;
    private ArrayList mStyleSheets;
    private ArrayList mScripts;

    // Input settings
    private String mInputType;
    private String mBaseURL;
    private String mFileRoot;
    private boolean mJavaScript;
    private boolean mXInclude;

    // Network settings
    private boolean mNetwork;
    private String mHttpUsername;
    private String mHttpPassword;
    private String mHttpProxy;

    // Log settings
    private String mLogFile;
    private boolean mVerbose;
    private boolean mDebug;

    // PDF settings
    private boolean mEmbedFonts;
    private boolean mSubsetFonts;
    private boolean mCompress;
    
    // Encryption settings
    private boolean mEncrypt;
    private int mKeyBits;
    private String mUserPassword;
    private String mOwnerPassword;
    private boolean mDisallowPrint;
    private boolean mDisallowModify;
    private boolean mDisallowCopy;
    private boolean mDisallowAnnotate;

    // Other command-line options
    private String mOptions;

    /** Constructor for Prince.
     * @param exePath The path of the Prince executable. (For example, this
     * may be <code>C:\Program&#xA0;Files\Prince\engine\bin\prince.exe</code>
     * on Windows or <code>/usr/bin/prince</code> on Linux).
     */
    public Prince(String exePath)
    {
	init(exePath, null);
    }
    
    /** Constructor for Prince.
     * @param exePath The path of the Prince executable. (For example, this
     * may be <code>C:\Program&#xA0;Files\Prince\engine\bin\prince.exe</code>
     * on Windows or <code>/usr/bin/prince</code> on Linux).
     * @param events An instance of the PrinceEvents interface that will
     * receive error/warning messages returned from Prince.
     */
    public Prince(String exePath, PrinceEvents events)
    {
	init(exePath, events);
    }

    private void init(String exePath, PrinceEvents events)
    {
	mEvents = events;
	mExePath = exePath;
	mStyleSheets = new ArrayList();
	mScripts = new ArrayList();

	// Input settings
	mInputType = "auto";
	mBaseURL = null;
	mFileRoot = null;
	mJavaScript = false;
	mXInclude = true;

	// Network settings
	mNetwork = true;
	mHttpUsername = null;
	mHttpPassword = null;
	mHttpProxy = null;

	// Log settings
	mLogFile = null;
	mVerbose = false;
	mDebug = false;
	
	// PDF settings
	mEmbedFonts = true;
	mSubsetFonts = true;
	mCompress = true;
	
	// Encryption settings
	mEncrypt = false;
	mKeyBits = 40;
	mUserPassword = "";
	mOwnerPassword = "";
	mDisallowPrint = false;
	mDisallowModify = false;
	mDisallowCopy = false;
	mDisallowAnnotate = false;

	// Other command-line options
	mOptions = null;
    }

    /**
     * Add a CSS style sheet that will be applied to each document.
     * @param cssPath The filename of the CSS style sheet.
     */
    public void addStyleSheet(String cssPath)
    {
	mStyleSheets.add(cssPath);
    }
    
    /**
     * Clear all of the CSS style sheets.
     */
    public void clearStyleSheets()
    {
        mStyleSheets.clear();
    }

    /**
     * Add a JavaScript script that will be executed before conversion.
     * @param jsPath The filename of the JavaScript script.
     */
    public void addScript(String jsPath)
    {
	mScripts.add(jsPath);
    }
    
    /**
     * Clear all of the JavaScript scripts.
     */
    public void clearScripts()
    {
        mScripts.clear();
    }

    /**
     * Specify whether documents should be parsed as HTML or XML/XHTML.
     * By default, all documents will be parsed as XML/XHTML, unless they have
     * a filename extension of ".html" or ".htm" and appear to contain HTML
     * rather than XML or XHTML. This method provides a way to override this
     * autodetection and insist that all documents should be parsed as HTML.
     * <p>
     * This is also necessary if a HTML document is passed to Prince from an
     * InputStream, as this has no filename and hence Prince will not check
     * the extension and will always treat it as XML/XHTML unless this method
     * has been called.
     * @param html True if all documents should be treated as HTML, false
     * otherwise.
     */
    public void setHTML(boolean html)
    {
	mInputType = (html ? "html" : "xml");
    }

    /**
     * Specify whether documents should be parsed as HTML or XML/XHTML.
     * By default, all documents will be parsed as XML/XHTML, unless they have
     * a filename extension of ".html" or ".htm" and appear to contain HTML
     * rather than XML or XHTML. This method provides a way to override this
     * autodetection and insist that all documents should be parsed as HTML or
     * XML.
     * <p>
     * This is also necessary if a HTML document is passed to Prince from an
     * InputStream, as this has no filename and hence Prince will not check
     * the extension and will always treat it as XML/XHTML unless this method
     * has been called.
     * @param inputType "auto", "html", or "xml"
     */
    public void setInputType(String inputType)
    {
	mInputType = inputType;
    }

    /**
     * Specify a file that Prince should use to log error/warning messages. If
     * this method if not called or if null is specified as the filename then
     * Prince will not write to any log. This method does not affect the
     * operation of the PrinceEvents interface, which will also receive
     * error/warning messages from Prince.
     * @param logfile The filename that Prince should use to log error/warning
     * messages, or null to disable logging.
     */
    public void setLog(String logfile)
    {
	mLogFile = logfile;
    }

    /**
     * Enable verbose logging of informative messages.
     * @param verbose True to enable verbose logging.
     */
    public void setVerbose(boolean verbose)
    {
	mVerbose = verbose;
    }

    /**
     * Enable logging of debug messages.
     * @param debug True to enable debug logging.
     */
    public void setDebug(boolean debug)
    {
	mDebug = debug;
    }

    /**
     * Specify the base URL of the input document. This can be used to
     * override the path of the input document, which is convenient when
     * processing local copies of a document from a website. It is also
     * helpful for specifying a base URL for documents that are provided via
     * an InputStream, as these documents have no natural base URL.
     * @param baseurl The base URL or path of the input document.
     */
    public void setBaseURL(String baseurl)
    {
	mBaseURL = baseurl;
    }

    /**
     * Specify the root directory for absolute filenames. This can be used
     * when converting a local file that uses absolute paths to refer to web
     * resources. For example, /images/logo.jpg can be rewritten to
     * /usr/share/images/logo.jpg by specifying "/usr/share" as the root.
     * @param fileRoot The path to prepend to absolute filenames.
     */
    public void setFileRoot(String fileRoot)
    {
	mFileRoot = fileRoot;
    }

    /**
     * Specify whether JavaScript scripts found in the document should be
     * executed during document conversion. Scripts will not be executed by
     * default unless explicitly enabled.
     * @param javascript True to enable JavaScript script execution.
     */
    public void setJavaScript(boolean javascript)
    {
	mJavaScript = javascript;
    }

    /**
     * Specify whether XML Inclusions (XInclude) processing should be applied
     * to input documents. XInclude processing will be performed by default
     * unless explicitly disabled.
     * @param xinclude False to disable XInclude processing.
     */
    public void setXInclude(boolean xinclude)
    {
	mXInclude = xinclude;
    }

    /**
     * Specify whether network access is allowed for downloading HTTP
     * resources. Network access is allowed by default unless explicitly
     * disabled.
     * @param network False to disable network access.
     */
    public void setNetwork(boolean network)
    {
	mNetwork = network;
    }

    /**
     * Specify the username for HTTP basic authentication.
     * @param username The username for HTTP basic authentication.
     */
    public void setHttpUsername(String username)
    {
	mHttpUsername = username;
    }

    /**
     * Specify the password for HTTP basic authentication.
     * @param password The password for HTTP basic authentication.
     */
    public void setHttpPassword(String password)
    {
	mHttpPassword = password;
    }

    /**
     * Specify the URL for the HTTP proxy server, if needed.
     * @param proxy The URL for the HTTP proxy server.
     */
    public void setHttpProxy(String proxy)
    {
	mHttpProxy = proxy;
    }

    /**
     * Specify whether fonts should be embedded in the output PDF file. Fonts
     * will be embedded by default unless explicitly disabled.
     * @param embedFonts False to disable PDF font embedding.
     */
    public void setEmbedFonts(boolean embedFonts)
    {
	mEmbedFonts = embedFonts;
    }

    /**
     * Specify whether embedded fonts should be subset to only include the
     * glyphs that are actually used in the PDF file. Embedded fonts will be
     * subset by default unless this is explicitly disabled.
     * @param subsetFonts False to disable subsetting of embedded fonts.
     */
    public void setSubsetFonts(boolean subsetFonts)
    {
	mSubsetFonts = subsetFonts;
    }

    /**
     * Specify whether compression should be applied to the output PDF file.
     * Compression will be applied by default unless explicitly disabled.
     * @param compress False to disable PDF compression.
     */
    public void setCompress(boolean compress)
    {
	mCompress = compress;
    }

    /**
     * Specify whether encryption should be applied to the output PDF file.
     * Encryption will not be applied by default unless explicitly enabled.
     * @param encrypt True to enable PDF encryption.
     */
    public void setEncrypt(boolean encrypt)
    {
	mEncrypt = encrypt;
    }
    
    /**
     * Set the parameters used for PDF encryption. Calling this method will
     * also enable PDF encryption, equivalent to calling
     * <code>setEncrypt(true)</code>.
     * @param keyBits The size of the encryption key in bits (must be 40 or
     * 128).
     * @param userPassword The user password for the encrypted PDF file.
     * @param ownerPassword The owner password for the encrypted PDF file.
     * @param disallowPrint True to disallow printing of the encrypted PDF
     * file.
     * @param disallowModify True to disallow modification of the encrypted
     * PDF file.
     * @param disallowCopy True to disallow copying from the encrypted PDF
     * file.
     * @param disallowAnnotate True to disallow annotation of the encrypted
     * PDF file.
     * @throws IllegalArgumentException if keyBits is not 40 or 128.
     */
    public void setEncryptInfo(int keyBits,
                               String userPassword,
                               String ownerPassword,
                               boolean disallowPrint,
                               boolean disallowModify,
                               boolean disallowCopy,
                               boolean disallowAnnotate)
    {
	if (keyBits != 40 && keyBits != 128)
	{
	    throw new IllegalArgumentException(
		    "invalid value for keyBits: "+keyBits+
		    " (must be 40 or 128)");
	}
	
	mEncrypt = true;
	mKeyBits = keyBits;
	mUserPassword = userPassword;
	mOwnerPassword = ownerPassword;
	mDisallowPrint = disallowPrint;
	mDisallowModify = disallowModify;
	mDisallowCopy = disallowCopy;
	mDisallowAnnotate = disallowAnnotate;
    }
    
    /**
     * Specify additional Prince command-line options.
     * @param options Additional command-line options, or null.
     */
    public void setOptions(String options)
    {
	mOptions = options;
    }

    /**
     * Convert an XML or HTML file to a PDF file. The name of the output PDF
     * file will be the same as the name of the input file but with an
     * extension of ".pdf".
     * @param xmlPath The filename of the input XML or HTML document.
     * @return True if a PDF file was generated successfully.
     */
    public boolean convert(String xmlPath)
	throws IOException
    {
	List cmdline = getCommandLine();

	cmdline.add("--server");
	cmdline.add(xmlPath);
        
	Process process = Util.invokeProcess(cmdline);
	
	return readMessages(process);
    }
    
    /**
     * Convert an XML or HTML file to a PDF file.
     * @param xmlPath The filename of the input XML or HTML document.
     * @param pdfPath The filename of the output PDF file.
     * @return True if a PDF file was generated successfully.
     */
    public boolean convert(String xmlPath, String pdfPath)
	throws IOException
    {
	List cmdline = getCommandLine();

	cmdline.add("--server");
	cmdline.add(xmlPath);
	cmdline.add(pdfPath);
        
	Process process = Util.invokeProcess(cmdline);
	
	return readMessages(process);
    }

    /**
     * Convert multiple XML or HTML files to a PDF file.
     * @param xmlPaths The filenames of the input XML or HTML documents.
     * @param pdfPath The filename of the output PDF file.
     * @return True if a PDF file was generated successfully.
     */
    public boolean convertMultiple(List xmlPaths, String pdfPath)
	throws IOException
    {
	List cmdline = getCommandLine();

	cmdline.add("--server");
	cmdline.add("--output="+pdfPath);

	for (int i = 0; i < xmlPaths.size(); ++i)
	{
	    String xmlPath = (String) xmlPaths.get(i);
	    cmdline.add(xmlPath);
	}
        
	Process process = Util.invokeProcess(cmdline);
	
	return readMessages(process);
    }

    /**
     * Convert an XML or HTML file to a PDF file. This method is useful for
     * servlets as it allows Prince to write the PDF output directly to the
     * OutputStream of the servlet response.
     * <p>
     * Note that no error/warning messages will be returned via the
     * PrinceEvents interface when calling this method. This is due to a
     * limitation of Prince that will be fixed in a future release. In the
     * meantime, we recommend the use of the <code>setLog()</code> method to
     * specify a log file that can be used to view error/warning messages from
     * Prince.
     * @param xmlPath The filename of the input XML or HTML document.
     * @param pdfOutput The OutputStream to which Prince will write the PDF
     * output.
     * @return True if a PDF file was generated successfully.
     */
    public boolean convert(String xmlPath, OutputStream pdfOutput)
	throws IOException
    {
	List cmdline = getCommandLine();

	cmdline.add("--server");
	cmdline.add("--silent");
	cmdline.add(xmlPath);
	cmdline.add("-o");
	cmdline.add("-");

	Process process = Util.invokeProcess(cmdline);

	InputStream outputFromPrince = process.getInputStream();

	// copy the PDF output from Prince stdout
	Util.copyInputToOutput(outputFromPrince, pdfOutput);

	// close Prince stdout
        outputFromPrince.close();

	return readMessages(process);
    }
    
    /**
     * Convert an XML or HTML file to a PDF file. This method is useful for
     * servlets as it allows Prince to write the PDF output directly to the
     * OutputStream of the servlet response.
     * <p>
     * Note that it may be helpful to specify a base URL or path for the input
     * document using the setBaseURL() method. This allows relative URLs and
     * paths in the document (eg. for images) to be resolved correctly.
     * <p>
     * Note that no error/warning messages will be returned via the
     * PrinceEvents interface when calling this method. This is due to a
     * limitation of Prince that will be fixed in a future release. In the
     * meantime, we recommend the use of the <code>setLog()</code> method to
     * specify a log file that can be used to view error/warning messages from
     * Prince.
     * @param xmlInput The InputStream from which Prince will read the XML or
     * HTML document.
     * @param pdfOutput The OutputStream to which Prince will write the PDF
     * output.
     * @return True if a PDF file was generated successfully.
     */
    public boolean convert(InputStream xmlInput, OutputStream pdfOutput)
	throws IOException
    {
	List cmdline = getCommandLine();

	cmdline.add("--server");
	cmdline.add("--silent");
	cmdline.add("-");

	Process process = Util.invokeProcess(cmdline);

	OutputStream inputToPrince = process.getOutputStream();
	InputStream outputFromPrince = process.getInputStream();

	// copy the XML input to Prince stdin
	Util.copyInputToOutput(xmlInput, inputToPrince);

	// close Prince stdin
	inputToPrince.close();

	// copy the PDF output from Prince stdout
	Util.copyInputToOutput(outputFromPrince, pdfOutput);

	// close Prince stdout
        outputFromPrince.close();

	return readMessages(process);
    }
    
    /**
     * Get the command line used to call Prince. The command line is returned
     * as a list of strings rather than a single string in order to avoid
     * potential problems with arguments that contain spaces.
     */
    private List getCommandLine()
    {
	List cmdline = new ArrayList();

	cmdline.add(mExePath);
	
	for (int i = 0; i < mStyleSheets.size(); ++i)
	{
	    String cssPath = (String) mStyleSheets.get(i);
	    cmdline.add("--style=" + cssPath);
	}

	for (int i = 0; i < mScripts.size(); ++i)
	{
	    String jsPath = (String) mScripts.get(i);
	    cmdline.add("--script=" + jsPath);
	}

	if (mInputType != null && !mInputType.equals("auto"))
	{
	    cmdline.add("--input="+mInputType);
	}

	if (mBaseURL != null)
	{
	    cmdline.add("--baseurl="+mBaseURL);
	}

	if (mFileRoot != null)
	{
	    cmdline.add("--fileroot="+mFileRoot);
	}

	if (mJavaScript)
	{
	    cmdline.add("--javascript");
	}

	if (!mXInclude)
	{
	    cmdline.add("--no-xinclude");
	}

	if (!mNetwork)
	{
	    cmdline.add("--no-network");
	}

	if (mHttpUsername != null)
	{
	    cmdline.add("--http-user="+mHttpUsername);
	}

	if (mHttpPassword != null)
	{
	    cmdline.add("--http-password="+mHttpPassword);
	}

	if (mHttpProxy != null)
	{
	    cmdline.add("--http-proxy="+mHttpProxy);
	}

	if (mLogFile != null)
	{
	    cmdline.add("--log="+mLogFile);
	}

	if (mVerbose)
	{
	    cmdline.add("--verbose");
	}

	if (mDebug)
	{
	    cmdline.add("--debug");
	}

	if (!mEmbedFonts)
	{
	    cmdline.add("--no-embed-fonts");
	}

	if (!mSubsetFonts)
	{
	    cmdline.add("--no-subset-fonts");
	}
	
	if (!mCompress)
	{
	    cmdline.add("--no-compress");
	}
	
	if (mEncrypt)
	{
	    cmdline.add("--encrypt");
	    cmdline.add("--key-bits=" + mKeyBits);

	    if (mUserPassword != null && !mUserPassword.equals(""))
	    {
		cmdline.add("--user-password=" + mUserPassword);
	    }

	    if (mOwnerPassword != null && !mOwnerPassword.equals(""))
	    {
		cmdline.add("--owner-password=" + mOwnerPassword);
	    }

	    if (mDisallowPrint)
	    {
		cmdline.add("--disallow-print");
	    }

	    if (mDisallowModify)
	    {
		cmdline.add("--disallow-modify");
	    }

	    if (mDisallowCopy)
	    {
		cmdline.add("--disallow-copy");
	    }

	    if (mDisallowAnnotate)
	    {
		cmdline.add("--disallow-annotate");
	    }
	}

	if (mOptions != null)
	{
	    cmdline.add(mOptions);
	}

	return cmdline;
    }

    /**
     * Read all of the messages from Prince stderr. Error and warning messages
     * will be dispatched to the PrinceEvents interface if one has been
     * provided.
     * @param process The Prince process.
     * @return True if Prince finished successfully.
     */
    private boolean readMessages(Process process)
	throws IOException
    {
        String line;
        String result;
        InputStream errMsgs;
        BufferedReader bufRead;
        
        errMsgs = process.getErrorStream();
        bufRead = new BufferedReader(new InputStreamReader(errMsgs));
        
        line = "";
        result = "";
        line = bufRead.readLine();

        while (line != null)
	{
	    if (line.length() >= 4)
	    {
		String msgTag = line.substring(0, 4);
		String msgBody = line.substring(4);
	    
		if (mEvents != null && msgTag.equals("msg|"))
		{
		    handleMessage(msgBody);
		}
		else if (msgTag.equals("fin|"))
		{
		    result = msgBody;
		}
		else
		{
		    // ignore unknown log messages
		}
	    }
	    else
	    {
		// ignore too short log messages
	    }
	    
            line = bufRead.readLine();
        }

        return result.equals("success");
    }
    
    /**
     * Handle a log message. The message is interpreted and passed to the
     * PrinceEvents interface provided by the caller.
     * @param msgBody The body of the message.
     */
    private void handleMessage(String msgBody)
    {
	if (msgBody.length() >= 4)
	{
	    String msgType = msgBody.substring(0, 3);
	    String tmpStr = msgBody.substring(4);

	    int locOffset = tmpStr.indexOf('|');
        
	    if (locOffset != -1)
	    {
		String msgLocation = tmpStr.substring(0, locOffset);
		String msgText = tmpStr.substring(locOffset);

		mEvents.onMessage(msgType, msgLocation, msgText);
	    }
	    else
	    {
		// ignore incorrectly formatted messages
	    }
	}
	else
	{
	    // ignore too short messages
	}
    }
}


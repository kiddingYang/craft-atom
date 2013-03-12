package org.craft.atom.protocol.http.model;

import java.nio.charset.Charset;

/**
 * Represents an http content type header value, it is useful for content codec.
 * <p>
 * Content type information consisting of a MIME type and an optional charset.
 * 
 * @author mindwind
 * @version 1.0, Mar 5, 2013
 */
public class HttpContentType {
	
	 public static final HttpContentType DEFAULT = new HttpContentType(MimeType.TEXT_HTML, Charset.forName("utf-8"));
		
	private final MimeType mimeType;
	private final Charset charset;
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	public HttpContentType(Charset charset) {
		this(null, charset);
	}
	
	public HttpContentType(MimeType mimeType) {
		this(mimeType, null);
	}

	public HttpContentType(MimeType mimeType, Charset charset) {
		this.mimeType = mimeType;
		this.charset = charset;
	}
	
	// ~ -----------------------------------------------------------------------------------------------------------

	public MimeType getMimeType() {
		return mimeType;
	}

	public Charset getCharset() {
		return charset;
	}
	
    public String toHttpString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.mimeType.toString());
        if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }

	@Override
	public String toString() {
		return String.format("HttpContentType [mimeType=%s, charset=%s]", mimeType, charset);
	}
	
}
/*
 *
 * This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
 * Authors: Balder Van Camp, Emiel Ackermann, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.tool.xml.html.tps;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.log.SysoLogger;
import com.itextpdf.tool.xml.exceptions.NoTagProcessorException;
import com.itextpdf.tool.xml.html.AbstractTagProcessor;
import com.itextpdf.tool.xml.html.Anchor;
import com.itextpdf.tool.xml.html.TagProcessor;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;

/**
 *
 * @author redlab_b
 *
 */
public class DefaultTagProcessorFactoryTest {


	/**
	 * @author itextpdf.com
	 *
	 */
	private final class TagProcessorImplementation extends AbstractTagProcessor {
	}

	private TagProcessorFactory tp;
	private TagProcessorImplementation tpi;

	@Before
	public void setup() {
		LoggerFactory.getInstance().setLogger(new SysoLogger(3));
		tp = new Tags().getHtmlTagProcessorFactory();
		tpi = new TagProcessorImplementation();

	}

	@Test
	public void testLoadDefaults() {
		assertTrue(tp.getProcessor("a", "") instanceof Anchor);
	}

	@Test(expected = NoTagProcessorException.class)
	public void loadFail() {
		tp.getProcessor("unknown", "");
	}

	@Test
	public void addTagProcessor() {
		tp.addProcessor(tpi, "addatag");
		TagProcessor processor = tp.getProcessor("addatag", "");
		Assert.assertEquals(tpi, processor);
	}
	@Test(expected = NoTagProcessorException.class)
	public void removeTagProcessor() {
		tp.addProcessor(tpi, "addatag");
		tp.removeProcessor("addatag");
		tp.getProcessor("addatag", "");
	}
}

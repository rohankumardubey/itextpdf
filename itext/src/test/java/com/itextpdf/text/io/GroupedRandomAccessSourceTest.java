/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS
    
    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/
    
    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.
    
    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.
    
    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.
    
    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.text.io;

import java.io.ByteArrayOutputStream;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GroupedRandomAccessSourceTest {
	byte[] data;
	
	@Before
	public void setUp() throws Exception {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < 100; i++){
			baos.write((byte)i);
		}
		
		data = baos.toByteArray();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGet() throws Exception {
		ArrayRandomAccessSource source1 = new ArrayRandomAccessSource(data);
		ArrayRandomAccessSource source2 = new ArrayRandomAccessSource(data);
		ArrayRandomAccessSource source3 = new ArrayRandomAccessSource(data);
		
		RandomAccessSource[] inputs = new RandomAccessSource[]{
				source1, source2, source3
		};
		
		GroupedRandomAccessSource grouped = new GroupedRandomAccessSource(inputs);
		
		Assert.assertEquals(source1.length() + source2.length() + source3.length(), grouped.length());

		Assert.assertEquals(source1.get(99),  grouped.get(99));
		Assert.assertEquals(source2.get(0),  grouped.get(100));
		Assert.assertEquals(source2.get(1),  grouped.get(101));
		Assert.assertEquals(source1.get(99),  grouped.get(99));
		Assert.assertEquals(source3.get(99),  grouped.get(299));

		Assert.assertEquals(-1, grouped.get(300));
	}

	private byte[] rangeArray(int start, int count){
		byte[] rslt = new byte[count];
		for(int i = 0; i < count; i++){
			rslt[i] = (byte)(i + start);
		}
		return rslt;
	}
	
	private void assertArrayEqual(byte[] a, int offa, byte[] b, int offb, int len){
		for(int i = 0; i < len; i++){
			if (a[i+offa] != b[i + offb]){
				throw new AssertionFailedError("Differ at index " + (i+offa) + " and " + (i + offb) + " -> " + a[i+offa] + " != " + b[i + offb]);
			}
			
		}
	}
	
	@Test
	public void testGetArray() throws Exception {
		ArrayRandomAccessSource source1 = new ArrayRandomAccessSource(data); // 0 - 99
		ArrayRandomAccessSource source2 = new ArrayRandomAccessSource(data); // 100 - 199
		ArrayRandomAccessSource source3 = new ArrayRandomAccessSource(data); // 200 - 299
		
		RandomAccessSource[] inputs = new RandomAccessSource[]{
				source1, source2, source3
		};
		
		GroupedRandomAccessSource grouped = new GroupedRandomAccessSource(inputs);

		byte[] out = new byte[500];

		Assert.assertEquals(300, grouped.get(0, out, 0, 300));
		assertArrayEqual(rangeArray(0, 100), 0, out, 0, 100);
		assertArrayEqual(rangeArray(0, 100), 0, out, 100, 100);
		assertArrayEqual(rangeArray(0, 100), 0, out, 200, 100);
		
		Assert.assertEquals(300, grouped.get(0, out, 0, 301));
		assertArrayEqual(rangeArray(0, 100), 0, out, 0, 100);
		assertArrayEqual(rangeArray(0, 100), 0, out, 100, 100);
		assertArrayEqual(rangeArray(0, 100), 0, out, 200, 100);
		
		Assert.assertEquals(100, grouped.get(150, out, 0, 100));
		assertArrayEqual(rangeArray(50, 50), 0, out, 0, 50);
		assertArrayEqual(rangeArray(0, 50), 0, out, 50, 50);
	}
	
	@Test
	public void testRelease() throws Exception{
		
		ArrayRandomAccessSource source1 = new ArrayRandomAccessSource(data); // 0 - 99
		ArrayRandomAccessSource source2 = new ArrayRandomAccessSource(data); // 100 - 199
		ArrayRandomAccessSource source3 = new ArrayRandomAccessSource(data); // 200 - 299
		
		RandomAccessSource[] sources = new RandomAccessSource[]{
				source1, source2, source3
		};
		
		final RandomAccessSource[] current = new RandomAccessSource[]{null};
		final int[] openCount = new int[]{0};
		GroupedRandomAccessSource grouped = new GroupedRandomAccessSource(sources){
			protected void sourceReleased(RandomAccessSource source) throws java.io.IOException {
				openCount[0]--;
				if (current[0] != source)
					throw new AssertionFailedError("Released source isn't the current source");
				current[0] = null;
			}
			
			protected void sourceInUse(RandomAccessSource source) throws java.io.IOException {
				if (current[0] != null)
					throw new AssertionFailedError("Current source wasn't released properly");
				openCount[0]++;
				current[0] = source;
			}
		};

		grouped.get(250);
		grouped.get(251);
		Assert.assertEquals(1, openCount[0]);
		grouped.get(150);
		grouped.get(151);
		Assert.assertEquals(1, openCount[0]);
		grouped.get(50);
		grouped.get(51);
		Assert.assertEquals(1, openCount[0]);
		grouped.get(150);
		grouped.get(151);
		Assert.assertEquals(1, openCount[0]);
		grouped.get(250);
		grouped.get(251);
		Assert.assertEquals(1, openCount[0]);

		grouped.close();
	}
}

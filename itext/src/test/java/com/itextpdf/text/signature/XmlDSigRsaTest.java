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
package com.itextpdf.text.signature;

import com.itextpdf.text.pdf.XfaXpathConstructor;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class XmlDSigRsaTest extends XmlDSigTest {

    public static final String KeyPairStore = "./src/test/resources/com/itextpdf/text/signature/ds/";
    public static final String Src = "./src/test/resources/com/itextpdf/text/signature/xfa.pdf";
    public static final String CmpDir = "./src/test/resources/com/itextpdf/text/signature/ds/";
    public static final String DestDir = "./target/com/itextpdf/test/signature/ds/";


    public static KeyPair loadKeyPair(String path, String algorithm) throws Exception {
        // Read Public Key.
        File filePublicKey = new File(path + "public.key");
        FileInputStream fis = new FileInputStream(path + "public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(path + "private.key");
        fis = new FileInputStream(path + "private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    @Before
    public void initialize() throws Exception {
        super.initialize();
        (new File(DestDir)).mkdirs();
        keyPair = loadKeyPair(KeyPairStore, "RSA");
    }

    KeyPair keyPair;

    @Test
    public void XmlDSigRSAWithPublicKey() throws Exception {

        String filename = "xfa.signed.ds.pk.pdf";
        String output = DestDir + filename;
        signDsWithPublicKey(Src, output, keyPair.getPrivate(), keyPair.getPublic(),
                DigestAlgorithms.SHA1, provider.getName());

        String cmp = saveXmlFromResult(output);

        Assert.assertTrue("Verification", verifyXmlDSig(cmp));
        Assert.assertTrue(compareXmls(cmp, CmpDir + filename.replace(".pdf", ".xml")));
    }

    @Test
    public void XmlDSigRSAWithKeyInfo() throws Exception {

        String filename = "xfa.signed.ds.ki.pdf";
        String output = DestDir + filename;

        signDsWithKeyInfo(Src, output, keyPair.getPrivate(), keyPair.getPublic(),
                DigestAlgorithms.SHA1, provider.getName());

        String cmp = saveXmlFromResult(output);
        Assert.assertTrue("Verification", verifyXmlDSig(cmp));
        Assert.assertTrue(compareXmls(cmp, CmpDir + filename.replace(".pdf", ".xml")));
    }

    @Test
    public void XmlDSigRSAWithPublicKeyPackage() throws Exception {

        String filename = "xfa.signed.ds.pk.package.pdf";
        String output = DestDir + filename;
        signPackageDsWithPublicKey(Src, output, XfaXpathConstructor.XdpPackage.Template, keyPair.getPrivate(), keyPair.getPublic(), DigestAlgorithms.SHA1, provider.getName());

        String cmp = saveXmlFromResult(output);
        Assert.assertTrue("Verification", verifyXmlDSig(cmp));
        Assert.assertTrue(compareXmls(cmp, CmpDir + filename.replace(".pdf", ".xml")));
    }

    @Test
    public void XmlDSigRSAWithKeyInfoPackage() throws Exception {

        String filename = "xfa.signed.ds.ki.package.pdf";
        String output = DestDir + filename;

        signPackageDsWithKeyInfo(Src, output, XfaXpathConstructor.XdpPackage.Template, keyPair.getPrivate(), keyPair.getPublic(), DigestAlgorithms.SHA1, provider.getName());

        String cmp = saveXmlFromResult(output);
        Assert.assertTrue("Verification", verifyXmlDSig(cmp));
        Assert.assertTrue(compareXmls(cmp, CmpDir + filename.replace(".pdf", ".xml")));
    }
}

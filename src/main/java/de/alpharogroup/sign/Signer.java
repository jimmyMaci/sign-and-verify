/**
 * The MIT License
 *
 * Copyright (C) 2015 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.alpharogroup.sign;

import de.alpharogroup.throwable.RuntimeExceptionDecorator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Objects;

/**
 * The class {@link Signer} provide sign algorithm for byte arrays
 */
public final class Signer
{

	/**
	 * The {@link Signature} object for signing
	 */
	private final Signature signature;

	/**
	 * The {@link SignatureBean} object holds the model data for signing
	 */
	private final SignatureBean signatureBean;

	/**
	 * Instantiates a new {@link Signer} object
	 *
	 * @param signatureBean the signature bean
	 */
	public Signer(SignatureBean signatureBean)
	{
		Objects.requireNonNull(signatureBean);
		Objects.requireNonNull(signatureBean.getPrivateKey());
		Objects.requireNonNull(signatureBean.getSignatureAlgorithm());
		this.signatureBean = signatureBean;
		this.signature = newSignature(this.signatureBean);
	}

	private Signature newSignature(final SignatureBean signatureBean) {
		return RuntimeExceptionDecorator.decorate(() -> {
			Signature signature = Signature.getInstance(signatureBean.getSignatureAlgorithm());
			signature.initSign(signatureBean.getPrivateKey());
			return signature;
		});
	}

	/**
	 * Sign the given byte array with the given private key and the appropriate algorithms
	 *
	 * @param bytesToSign the bytes to sign
	 * @return the signature as byte array
	 */
	public synchronized byte[] sign(byte[] bytesToSign)
	{
		return RuntimeExceptionDecorator.decorate(() -> {
			signature.update(bytesToSign);
			return signature.sign();
		});
	}

}

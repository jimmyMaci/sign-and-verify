package de.alpharogroup.sign;

import de.alpharogroup.io.Serializer;
import de.alpharogroup.throwable.RuntimeExceptionDecorator;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Base64;
import java.util.Objects;

/**
 * The class {@link ObjectVerifier} provides an algorithm  for java serializable objects with given
 * signed string arrays
 */
public final class ObjectVerifier<T extends Serializable>
{

	/**
	 * The {@link Signature} object for the verification
	 */
	private final Signature signature;

	/**
	 * The {@link VerifyBean} object holds the model data for the verification
	 */
	private final VerifyBean verifyBean;

	/**
	 * Instantiates a new {@link ObjectVerifier} object
	 *
	 * @param verifyBean The {@link VerifyBean} object holds the model data for verifying
	 */
	public ObjectVerifier(VerifyBean verifyBean)
	{
		Objects.requireNonNull(verifyBean);
		Objects.requireNonNull(verifyBean.getSignatureAlgorithm());
		if (verifyBean.getPublicKey() == null && verifyBean.getCertificate() == null)
		{
			throw new IllegalArgumentException("Please provide a public key or certificate");
		}
		this.verifyBean = verifyBean;
		try
		{
			this.signature = Signature.getInstance(this.verifyBean.getSignatureAlgorithm());
			if (verifyBean.getPublicKey() != null)
			{
				signature.initVerify(this.verifyBean.getPublicKey());
			}
			else
			{
				signature.initVerify(this.verifyBean.getCertificate());
			}
		}
		catch (InvalidKeyException | NoSuchAlgorithmException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Verify the given byte array with the given signed byte array
	 *
	 * @param bytesToVerify the bytes to verify
	 * @param signedBytes   the signed byte array
	 * @return true, if successful otherwise false
	 */
	public synchronized boolean verify(T bytesToVerify, String signedBytes)
	{
		if (verifyBean.getPublicKey() != null)
		{
			return verifyWithPublicKey(bytesToVerify, signedBytes);
		}
		return verifyWithCertificate(bytesToVerify, signedBytes);
	}

	/**
	 * Verify the given byte array with the given signed byte array with the certificate of the
	 * verifyBean and the appropriate algorithms.
	 *
	 * @param bytesToVerify the bytes to verify
	 * @param signedBytes   the signed byte array
	 * @return true, if successful otherwise false
	 */
	private synchronized boolean verifyWithCertificate(T bytesToVerify, String signedBytes)
	{
		return RuntimeExceptionDecorator.decorate(() -> {
			signature.initVerify(verifyBean.getCertificate());
			signature.update(Serializer.toByteArray(bytesToVerify));
			byte[] signedBytesBytes = Base64.getDecoder().decode(signedBytes);
			return signature.verify(signedBytesBytes);
		});
	}

	/**
	 * Verify the given byte array with the given signed byte array with the public key of the
	 * verifyBean and the appropriate algorithms.
	 *
	 * @param bytesToVerify the bytes to verify
	 * @param signedBytes   the signed byte array
	 * @return true, if successful otherwise false
	 */
	private synchronized boolean verifyWithPublicKey(T bytesToVerify, String signedBytes)
	{
		return RuntimeExceptionDecorator.decorate(() -> {
			signature.initVerify(verifyBean.getPublicKey());
			signature.update(Serializer.toByteArray(bytesToVerify));
			byte[] signedBytesBytes = Base64.getDecoder().decode(signedBytes);
			return signature.verify(signedBytesBytes);
		});
	}

}

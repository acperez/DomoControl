#include <jni.h>
//#include <string.h>
#include <string>
#include <stdio.h>
#include <sys/system_properties.h>

#include <openssl/blowfish.h>
#include <openssl/evp.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>

#include "AndroidLog.h"

#define KEY_SIZE 32
#define VECTOR_SIZE 8

unsigned char key[KEY_SIZE];
unsigned char vector[VECTOR_SIZE];

bool keyGenerated = false;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jbyteArray JNICALL
Java_es_acperez_domocontrol_DomoControlApplication_EncryptData(JNIEnv * env, jobject  obj, jbyteArray rawData);

JNIEXPORT jbyteArray JNICALL
Java_es_acperez_domocontrol_DomoControlApplication_DecryptData(JNIEnv * env, jobject  obj, jbyteArray rawData);

#ifdef __cplusplus
}
#endif

#define OP_SIZE 1032

int encrypt(unsigned char *key, unsigned char *vector, unsigned char *data, int data_len, unsigned char **data_enc)
{
	unsigned char outbuf[OP_SIZE];
	int olen, tlen;
	EVP_CIPHER_CTX ctx;
	EVP_CIPHER_CTX_init (&ctx);
	EVP_EncryptInit (&ctx, EVP_bf_cbc (), key, vector);

	bzero (&outbuf, OP_SIZE);

	if (EVP_EncryptUpdate (&ctx, outbuf, &olen, data, data_len) != 1)
		return 0;

	if (EVP_EncryptFinal (&ctx, outbuf + olen, &tlen) != 1)
		return 0;

	olen += tlen;

	EVP_CIPHER_CTX_cleanup (&ctx);

	*data_enc = new unsigned char[olen];
	memcpy(*data_enc, outbuf, olen);

	return olen;
}

int decrypt(unsigned char *key, unsigned char *vector, unsigned char *data, int data_len, unsigned char **data_raw)
{
	unsigned char outbuf[OP_SIZE];
	int olen, tlen;
	EVP_CIPHER_CTX ctx;
	EVP_CIPHER_CTX_init (&ctx);
	EVP_DecryptInit (&ctx, EVP_bf_cbc (), key, vector);

	bzero (&outbuf, OP_SIZE);

	if (EVP_DecryptUpdate (&ctx, outbuf, &olen, data, data_len) != 1)
		return 0;

	if (EVP_DecryptFinal (&ctx, outbuf + olen, &tlen) != 1)
		return 0;

	olen += tlen;

	EVP_CIPHER_CTX_cleanup (&ctx);

	*data_raw = new unsigned char[olen];
	memcpy(*data_raw, outbuf, olen);

	return olen;
}

void GetKey(){
	if(keyGenerated)
		return;

	int fd;
	std::string s;
	long bufferLen = 1024;
	char* buffer;
	size_t result;

	char serial[32];
	serial[0] = 0;
	__system_property_get("ro.serialno", serial);
	s.append(serial);

	if ((fd = open ("/proc/cpuinfo", O_RDONLY)) != -1)
	{
			buffer = (char *)malloc((bufferLen + 1) * sizeof(char));
			do
			{
					result = read (fd, buffer, bufferLen);
					buffer[result] = '\0';
					s.append(buffer);
			}
			while(result);

			free(buffer);
			close(fd);
	}

	while(s.length() < KEY_SIZE + VECTOR_SIZE)
		s.append(s);

	key[KEY_SIZE] = 0;
	memcpy(key, s.substr(0,KEY_SIZE - 1).c_str(), KEY_SIZE);

	vector[VECTOR_SIZE] = 0;
	memcpy(vector, s.substr(KEY_SIZE - 1, KEY_SIZE + VECTOR_SIZE -2).c_str(), VECTOR_SIZE);

	keyGenerated = true;
}

JNIEXPORT jbyteArray JNICALL
Java_es_acperez_domocontrol_DomoControlApplication_EncryptData(JNIEnv * env, jobject  obj, jbyteArray rawData)
{
	GetKey();

	int len = env->GetArrayLength(rawData);
	jbyte *jData = env->GetByteArrayElements(rawData, NULL);
	unsigned char* data = new unsigned char[len + 1];
	data[len] = 0;
	memcpy(data, jData, len);
	env->ReleaseByteArrayElements(rawData, jData, 0);

	unsigned char *output;
	len = encrypt(key, vector, data, len, &output);

	jbyteArray byte= env->NewByteArray(len);
	env->SetByteArrayRegion(byte, 0, len, (jbyte *)output);

	return byte;
}

JNIEXPORT jbyteArray JNICALL
Java_es_acperez_domocontrol_DomoControlApplication_DecryptData(JNIEnv * env, jobject  obj, jbyteArray rawData)
{
	GetKey();

	int len = env->GetArrayLength(rawData);
	jbyte *jData = env->GetByteArrayElements(rawData, NULL);
	unsigned char* data = new unsigned char[len + 1];
	data[len] = 0;
	memcpy(data, jData, len);
	env->ReleaseByteArrayElements(rawData, jData, 0);

	unsigned char *output;
	len = decrypt(key, vector, data, len, &output);

	jbyteArray byte= env->NewByteArray(len);
	env->SetByteArrayRegion(byte, 0, len, (jbyte *)output);

	return byte;
}

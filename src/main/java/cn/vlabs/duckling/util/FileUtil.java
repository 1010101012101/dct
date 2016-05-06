/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package cn.vlabs.duckling.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.apache.log4j.Logger;

/**
 * Introduction Here.
 * 
 * @date May 7, 2010
 * @author zzb
 */
public class FileUtil {
	private FileUtil() {
	};

	private static final int BUFFER_SIZE = 4096;
	private static final Logger log = Logger.getLogger(FileUtil.class);

	public static boolean deleteFilesInFolder(String strFolder) {
		boolean flag = false;
		File folder = new File(strFolder);
		if (!folder.exists()) {
			return flag;
		}
		String[] files = folder.list();
		for (int i = 0; i < files.length; i++) {
			String fileName = strFolder + File.separator + files[i];
			File file = new File(fileName);
			if (file.isDirectory()) {
				flag = deleteFolders(fileName);
			} else {
				file.delete();
			}
		}
		return flag;
	}

	public static boolean deleteFolders(String strFolder) {
		boolean flag = false;
		File folder = new File(strFolder);
		if (!folder.isDirectory()) {
			if (log.isDebugEnabled()) {
				log.debug("the folder: " + strFolder
						+ " is not a correct folder");
			}
			return flag;
		}
		flag = deleteFilesInFolder(strFolder);
		folder.delete();
		return flag;
	}

	/**
	 * Just copies all bytes from <I>in</I> to <I>out</I>. The copying is
	 * performed using a buffer of bytes.
	 * 
	 * @since 1.9.31
	 * @param in
	 *            The inputstream to copy from
	 * @param out
	 *            The outputstream to copy to
	 * @throws IOException
	 *             In case reading or writing fails.
	 */
	public static void copyContents(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[BUFFER_SIZE];
		int bytesRead = 0;

		while ((bytesRead = in.read(buf)) > 0) {
			out.write(buf, 0, bytesRead);
		}

		out.flush();
	}

	/**
	 * Just copies all characters from <I>in</I> to <I>out</I>. The copying is
	 * performed using a buffer of bytes.
	 * 
	 * @since 1.5.8
	 * @param in
	 *            The reader to copy from
	 * @param out
	 *            The reader to copy to
	 * @throws IOException
	 *             If reading or writing failed.
	 */
	public static void copyContents(Reader in, Writer out) throws IOException {
		char[] buf = new char[BUFFER_SIZE];
		int bytesRead = 0;

		while ((bytesRead = in.read(buf)) > 0) {
			out.write(buf, 0, bytesRead);
		}

		out.flush();
	}

	/**
	 * Returns the full contents of the Reader as a String.
	 * 
	 * @since 1.5.8
	 * @param in
	 *            The reader from which the contents shall be read.
	 * @return String read from the Reader
	 * @throws IOException
	 *             If reading fails.
	 */
	public static String readContents(Reader in) throws IOException {
		Writer out = null;

		try {
			out = new StringWriter();

			copyContents(in, out);

			return out.toString();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				log.error("Not able to close the stream while reading contents.");
			}
		}
	}

	/**
	 * Reads in file contents.
	 * <P>
	 * This method is smart and falls back to ISO-8859-1 if the input stream
	 * does not seem to be in the specified encoding.
	 * 
	 * @param input
	 *            The InputStream to read from.
	 * @param encoding
	 *            The encoding to assume at first.
	 * @return A String, interpreted in the "encoding", or, if it fails, in
	 *         Latin1.
	 * @throws IOException
	 *             If the stream cannot be read or the stream cannot be decoded
	 *             (even) in Latin1
	 */
	public static String readContents(InputStream input, String encoding)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileUtil.copyContents(input, out);

		ByteBuffer bbuf = ByteBuffer.wrap(out.toByteArray());

		Charset cset = Charset.forName(encoding);
		CharsetDecoder csetdecoder = cset.newDecoder();

		csetdecoder.onMalformedInput(CodingErrorAction.REPORT);
		csetdecoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		try {
			CharBuffer cbuf = csetdecoder.decode(bbuf);

			return cbuf.toString();
		} catch (CharacterCodingException e) {
			Charset latin1 = Charset.forName("ISO-8859-1");
			CharsetDecoder l1decoder = latin1.newDecoder();

			l1decoder.onMalformedInput(CodingErrorAction.REPORT);
			l1decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

			try {
				bbuf = ByteBuffer.wrap(out.toByteArray());

				CharBuffer cbuf = l1decoder.decode(bbuf);

				return cbuf.toString();
			} catch (CharacterCodingException ex) {
				throw (CharacterCodingException) ex.fillInStackTrace();
			}
		}
	}

	/**
	 * Brief Intro Here 以字符串形式读取文件内容
	 * 
	 * @param file
	 *            需要读的文件
	 * @return 文件内容
	 */
	public static String getFileContent(File file) {
		return getFileContent(file, "UTF-8");
	}

	/**
	 * Brief Intro Here 以字符串形式读取文件内容
	 * 
	 * @param file
	 *            需要读的文件
	 * @param charset
	 *            指定读取的字符集
	 * @return 文件内容
	 */
	public static String getFileContent(File file, String charset) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), charset));
			String line = br.readLine();
			while (line != null) {
				sb.append(line).append("\n\r");
				line = br.readLine();
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Brief Intro Here 默认UTF-8字符编码
	 * 
	 * @param file
	 *            需要写的文件
	 * @param content
	 *            需要写的文件内容
	 * @return 写文件是否成功
	 */
	public static boolean writeFile(File file, String content) {
		return writeFile(file, content, Charset.forName("UTF-8"));
	}

	/**
	 * Brief Intro Here
	 * 
	 * @param file
	 *            需要写的文件
	 * @param content
	 *            需要写的文件内容
	 * @param charset
	 *            指定字符编码
	 * @return 写文件是否成功
	 */
	public static boolean writeFile(File file, String content, Charset charset) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), charset));
			bw.write(content);
			bw.flush();
			bw.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Brief Intro Here 复制文件
	 * 
	 * @param
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	/**
	 * Brief Intro Here 复制文件夹
	 * 
	 * @param
	 */
	public static void copyDirectory(String sourcePath, String targetPath)
			throws IOException {
		// 新建目标目录
		File targetDir = new File(targetPath);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// 获取源文件夹当前下的文件或目录
		File[] files = (new File(sourcePath)).listFiles();
		if (files != null) {
			for (File sourceFile : files) {
				if (sourceFile.isFile()) {
					// 目标文件
					File targetFiled = new File(
							new File(targetPath).getAbsolutePath()
									+ File.separator + sourceFile.getName());
					copyFile(sourceFile, targetFiled);
				}
				if (sourceFile.isDirectory()) {
					// 准备复制的源文件夹
					String dir1 = sourcePath + File.separator
							+ sourceFile.getName();
					// 准备复制的目标文件夹
					String dir2 = targetPath + File.separator
							+ sourceFile.getName();
					copyDirectory(dir1, dir2);
				}
			}
		}
	}

	public static void copyInputStreamToFile(InputStream in, File file)
			throws IOException {
		File path = file.getParentFile();
		if (!path.exists()) {
			path.mkdirs();
		}

		byte[] buff = new byte[1024];
		int readed = 0;
		FileOutputStream out = new FileOutputStream(file);
		try {
			while ((readed = in.read(buff)) != -1) {
				out.write(buff, 0, readed);
			}
		} finally {
			out.close();
		}
	}

}

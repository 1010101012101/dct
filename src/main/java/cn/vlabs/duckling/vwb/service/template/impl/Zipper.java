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

package cn.vlabs.duckling.vwb.service.template.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 压缩工具包
 * 
 * @date 2013-4-1
 * @author xiejj@cstnet.cn
 */
public class Zipper {
	private static Logger log = Logger.getLogger(Zipper.class);
	public static void zip(String sourceDir, String zipFile) {
		OutputStream os;
		try {
			os = new FileOutputStream(zipFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			ZipOutputStream zos = new ZipOutputStream(bos);

			File file = new File(sourceDir);

			String basePath = null;
			if (file.isDirectory()) {
				basePath = file.getPath();
			} else {
				basePath = file.getParent();
			}

			zipFile(file, basePath, zos);

			zos.closeEntry();
			zos.close();
		} catch (Exception e) {
			log.error(String.format("zip file from %s to %s failed.",sourceDir,zipFile),e);
		}

	}

	/**
	 * 
	 * create date:2009- 6- 9 author:Administrator
	 * 
	 * @param source
	 * @param basePath
	 * @param zos
	 * @throws IOException
	 */
	private static void zipFile(File source, String basePath,
			ZipOutputStream zos) {
		File[] files = new File[0];

		if (source.isDirectory()) {
			files = source.listFiles();
		} else {
			files = new File[1];
			files[0] = source;
		}

		String pathName;
		byte[] buf = new byte[1024];
		int length = 0;
		try {
			for (File file : files) {
				if (file.isDirectory()) {
					pathName = file.getPath().substring(basePath.length() + 1)
							+ "/";
					zos.putNextEntry(new ZipEntry(pathName));
					zipFile(file, basePath, zos);
				} else {
					pathName = file.getPath().substring(basePath.length() + 1);
					InputStream is = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(is);
					zos.putNextEntry(new ZipEntry(pathName));
					while ((length = bis.read(buf)) > 0) {
						zos.write(buf, 0, length);
					}
					is.close();
				}
			}
		} catch (Exception e) {
			log.error(String.format("zip file from %s to OutputStream failed.",source.getAbsolutePath()),e);
		}

	}

	/**
	 * 解压 zip 文件，注意不能解压 rar 文件哦，只能解压 zip 文件 解压 rar 文件 会出现 java.io.IOException:
	 * Negative seek offset 异常 create date:2009- 6- 9 author:Administrator
	 * 
	 * @param zipfile
	 *            zip 文件，注意要是正宗的 zip 文件哦，不能是把 rar 的直接改为 zip 这样会出现
	 *            java.io.IOException: Negative seek offset 异常
	 * @param destDir
	 * @throws IOException
	 */
	public static void unZip(String zipfile, String destDir) {

		destDir = destDir.endsWith("//") ? destDir : destDir + "//";
		byte b[] = new byte[1024];
		int length;

		ZipFile zipFile;
		try {
			zipFile = new ZipFile(new File(zipfile));
			Enumeration<?> enumeration = zipFile.getEntries();
			ZipEntry zipEntry = null;

			while (enumeration.hasMoreElements()) {
				zipEntry = (ZipEntry) enumeration.nextElement();
				File loadFile = new File(destDir + zipEntry.getName());

				if (zipEntry.isDirectory()) {
					// 这段都可以不要，因为每次都貌似从最底层开始遍历的
					loadFile.mkdirs();
				} else {
					if (!loadFile.getParentFile().exists())
						loadFile.getParentFile().mkdirs();

					OutputStream outputStream = new FileOutputStream(loadFile);
					InputStream inputStream = zipFile.getInputStream(zipEntry);

					while ((length = inputStream.read(b)) > 0)
						outputStream.write(b, 0, length);

				}
			}
			log.info("文件"+zipfile+"解压成功");
		} catch (IOException e) {
			log.error(String.format("解压文件 %s 到 %s 失败.", zipfile, destDir),e);
		}
	}
};

package mmmlibx.lib;

import littleMaidMobX.LittleMaidMobX;
import mmmlibx.lib.rewrite.RewritedFileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * modsディレクトリに存在するファイルを処理するための基本クラス。
 * 特にMOD化されていないアーカイブも追加されているので、リソースの扱いとしてはファイル名だけで良いはず。
 *
 */
public abstract class FileLoaderBase {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * 処理を実行
	 */
	public void execute() {
//		List<File> llist = FileManager.getAllmodsFiles();
		List<File> files = RewritedFileManager.getAllFiles(getClass().getClassLoader());//FileManager.getAllmodsFiles(getClass().getClassLoader(), true);
		for (File lf : files) {
			String ls = lf.getName();
			if (isZipLoad() && ls.matches("(.+).(zip|jar)$")) {
				decodeZip(lf);
			} if (lf.isDirectory()) {
				decodeDir(lf, lf);
			} else {
				try {
					preLoad(lf, ls, Files.newInputStream(lf.toPath()));
				} catch (Exception e) {
					LOGGER.warn("execute error", e);
				}
			}
		}
	}

	/**
	 * Zipの解析
	 * @param pFile
	 */
	public void decodeZip(File pFile) {
		try {
			ZipFile lzf = new ZipFile(pFile);
			FileInputStream lfis = new FileInputStream(pFile);
			ZipInputStream lzis = new ZipInputStream(lfis);
			
			for (ZipEntry lze = lzis.getNextEntry(); lze != null; lze = lzis.getNextEntry()) {
				if (!lze.isDirectory()) {
					preLoad(pFile, lze.getName(), lzf.getInputStream(lze));
				}
			}
			
			lzis.close();
			lfis.close();
			lzf.close();
		} catch (Exception e) {
			LOGGER.warn("decodeZip error", e);
		}
	}

	/**
	 * ディレクトリの解析
	 * @param pFile
	 */
	public void decodeDir(File pBaseDir, File pFile) {
		for (File lf : pFile.listFiles()) {
			if (lf.isDirectory()) {
				decodeDir(pBaseDir, lf);
			} else {
				try {
					preLoad(lf, lf.getAbsolutePath().substring(pBaseDir.getAbsolutePath().length()), Files.newInputStream(lf.toPath()));
				} catch (Exception e) {
					LOGGER.warn("decodeDir error", e);
				}
			}
		}
	}

	/**
	 * zipファイルを解析するか？
	 * @return
	 */
	public boolean isZipLoad() {
		return true;
	}

	/**
	 * 解析動作の実装
	 * @param pFile
	 * @param pFileName
	 * @return
	 */
	public boolean load(File pFile, String pFileName, InputStream pInputStream) {
		System.out.println("load# " + pFile.getPath() + " # " + pFileName);
		return false;
	}

	protected boolean preLoad(File pFile, String pFileName, InputStream pInputStream) {
//		if (!pFileName.startsWith("/")) {
//			pFileName = (new StringBuilder()).append("/").append(pFileName).toString();
//		} else {
//		}
		pFileName = pFileName.replace("\\", "/");
		return load(pFile, pFileName, pInputStream);
	}

}

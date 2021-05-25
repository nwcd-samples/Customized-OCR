package cn.nwcdcloud.samples.ocr.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.nwcdcloud.samples.ocr.service.ManifestService;

@Service
public class ManifestServiceImpl implements ManifestService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Attributes attributes;

	@PostConstruct
	public void init() {
		InputStream isManifest = this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
		try {
			Manifest manifest = new Manifest(isManifest);
			attributes = manifest.getMainAttributes();
			isManifest.close();
		} catch (IOException e) {
			logger.warn("读取版本报错", e);
		}
	}

	@Override
	public String get(String key) {
		return attributes.getValue(key);
	}
}

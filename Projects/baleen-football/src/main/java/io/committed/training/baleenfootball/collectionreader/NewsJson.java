package io.committed.training.baleenfootball.collectionreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.training.baleenfootball.utils.AbstractConfigurableFolderReader;
import uk.gov.dstl.baleen.uima.UimaSupport;

public class NewsJson extends AbstractConfigurableFolderReader {

  // Allow newlines inside strings since thats the data we have
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

  @Override
  protected boolean isFileIncluded(Path path) {
    // The News output is currently a guid
    // eg 0aa70355-e5bd-473c-9e94-bc2130d4745c
    // but that seems an unnecessary constraint
    // so we just return true here to consider any file

    return true;
  }

  @Override
  protected boolean processFile(JCas jCas, Path file) {

    try {
      Article article = readFile(file);
      saveToJCas(jCas, article);

      return true;
    } catch (Exception e) {
      getMonitor().warn("Unable to read {}", file, e);
      return false;
    }
  }

  private Article readFile(Path file) throws IOException {
    final byte[] bytes = Files.readAllBytes(file);

    final Article a = OBJECT_MAPPER.readValue(bytes, Article.class);
    a.setLocalFilename(file.toFile().getAbsolutePath());

    return a;
  }

  private void saveToJCas(JCas jCas, Article a) {
    // Set the document text
    jCas.setDocumentText(a.getContent());

    // The document annotation is a special place that Baleen stores
    // its own important per document metadata.

    final DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
    da.setTimestamp(a.getBestTimestamp());
    da.setDocType(a.getSourceType());
    da.setDocumentClassification("O");
    da.setSourceUri(a.getBestSource());

    // Add some meta
    // but sourceeType -> da.getDocType
    // description -> summary

    addMetadata(jCas, "summary", a.getSummary().orElse(null));
    addMetadata(jCas, "title", a.getTitle());
    addMetadata(jCas, "url", a.getUrl());
    addMetadata(jCas, "sourceUrl", a.getSourceUrl());
    addMetadata(jCas, "localFilename", a.getLocalFilename());
  }



}

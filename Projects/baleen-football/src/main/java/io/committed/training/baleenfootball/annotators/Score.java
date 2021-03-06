package io.committed.training.baleenfootball.annotators;

import java.util.Collections;
import java.util.regex.Matcher;
import org.apache.uima.jcas.JCas;
import com.google.common.collect.ImmutableSet;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Buzzword;

public class Score extends AbstractRegexAnnotator<Buzzword> {

  private static final String PATTERN = "\\s[0-9]\\s?-\\s?[0-9]\\s";

  public Score() {
    super(PATTERN, false, 1.0);
  }

  @Override
  protected Buzzword create(JCas jCas, Matcher matcher) {
    Buzzword b = new Buzzword(jCas);
    b.setSubType("score");
    return b;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Buzzword.class));
  }

}

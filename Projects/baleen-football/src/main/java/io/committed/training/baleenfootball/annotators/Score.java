package io.committed.training.baleenfootball.annotators;

import java.util.regex.Matcher;
import org.apache.uima.jcas.JCas;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Buzzword;

public class Score extends AbstractRegexAnnotator<Buzzword> {

  public Score() {
    super("TODO", false, 1.0);
  }

  @Override
  protected Buzzword create(JCas jCas, Matcher matcher) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AnalysisEngineAction getAction() {
    // TODO Auto-generated method stub
    return null;
  }

}
package io.committed.training.baleenfootball.annotators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import com.google.common.collect.ImmutableSet;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

public class FootballEvent extends BaleenAnnotator {

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    JCasUtil.select(jCas, Sentence.class).forEach(s -> processSentence(jCas, s));
  }

  private void processSentence(JCas jCas, Sentence s) {

    List<Buzzword> buzzwords = JCasUtil.selectCovered(Buzzword.class, s).stream()
        .filter(b -> "football".equals(b.getSubType())).collect(Collectors.toList());
    List<Location> locations = JCasUtil.selectCovered(Location.class, s);

    if (buzzwords.isEmpty() || locations.isEmpty()) {
      return;
    }

    List<Entity> entities = new ArrayList<>();

    // Get football related aspects in the sentence
    buzzwords.forEach(entities::add);
    locations.forEach(entities::add);

    // If no entities in this sentence we've got nothing to add?
    if (entities.isEmpty()) {
      return;
    }


    Event event = new Event(jCas);
    event.setEventType(UimaTypesUtils.toArray(jCas, Collections.singleton("football")));
    event.setEntities(UimaTypesUtils.toFSArray(jCas, entities));
    event.addToIndexes();


  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Buzzword.class, Location.class),
        ImmutableSet.of(Buzzword.class));
  }

}

package de.plushnikov.intellij.plugin.processor.clazz.log;

import com.intellij.openapi.components.Service;
import de.plushnikov.intellij.plugin.LombokClassNames;

/**
 * @author Plushnikov Michail
 */
@Service
public final class Log4jProcessor extends AbstractTopicSupportingSimpleLogProcessor {

  private static final String LOGGER_TYPE = "org.apache.log4j.Logger";
  private static final String LOGGER_INITIALIZER = "org.apache.log4j.Logger.getLogger(%s)";

  public Log4jProcessor() {
    super(LombokClassNames.LOG_4_J, LOGGER_TYPE, LOGGER_INITIALIZER, LoggerInitializerParameter.TYPE);
  }
}

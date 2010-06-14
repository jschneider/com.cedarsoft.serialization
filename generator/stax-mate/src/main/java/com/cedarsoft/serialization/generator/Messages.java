package com.cedarsoft.serialization.generator;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 */
public enum Messages {
  UNEXPECTED_NGCC_TOKEN, // 3 args
  BASEDIR_DOESNT_EXIST, // 1 arg
  USAGE, //0 args
  VERSION, // 0 args
  NON_EXISTENT_FILE, // 1 arg
  UNRECOGNIZED_PARAMETER, //1 arg
  OPERAND_MISSING, // 1 arg
  ;

  @NotNull
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( Messages.class.getPackage().getName() + ".MessageBundle" );

  public String format( Object... args ) {
    return MessageFormat.format( RESOURCE_BUNDLE.getString( name() ), args );
  }
}

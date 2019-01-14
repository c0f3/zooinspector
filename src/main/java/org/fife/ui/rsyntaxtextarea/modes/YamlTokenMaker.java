package org.fife.ui.rsyntaxtextarea.modes;

import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

/**
 *
 * @author dbridges
 */
public class YamlTokenMaker extends AbstractTokenMaker {
  
  protected int currentTokenStart = 0;
  protected int currentTokenType  = 0;
  
  protected boolean leftHandSide = true;
  
  // This is used for the "name" values to the left of the colon
  int YAML_IDENTIFIER = Token.MARKUP_TAG_NAME;
  //int YAML_IDENTIFIER = Token.MARKUP_TAG_ATTRIBUTE;

  @Override
  public TokenMap getWordsToHighlight() {
   TokenMap tokenMap = new TokenMap();

   tokenMap.put("---", Token.RESERVED_WORD);
   tokenMap.put("...", Token.RESERVED_WORD);
   tokenMap.put("&", Token.RESERVED_WORD);
   tokenMap.put("*", Token.RESERVED_WORD);
   tokenMap.put(">", Token.RESERVED_WORD);
   
//   tokenMap.put("test1", Token.VARIABLE);
//   tokenMap.put("test2", Token.FUNCTION);
//   tokenMap.put("test3", Token.LITERAL_CHAR);
//   tokenMap.put("test4", Token.MARKUP_TAG_ATTRIBUTE);
//   tokenMap.put("test5", Token.ANNOTATION);
//   tokenMap.put("test6", Token.DATA_TYPE);
//   tokenMap.put("test7", Token.MARKUP_TAG_NAME);
//   tokenMap.put("test8", Token.RESERVED_WORD_2);
   
   tokenMap.put("true", Token.LITERAL_BOOLEAN);
   tokenMap.put("false", Token.LITERAL_BOOLEAN);

   tokenMap.put("#", Token.COMMENT_EOL);

   tokenMap.put("-", Token.OPERATOR);
   tokenMap.put(":", Token.OPERATOR);
   tokenMap.put("[", Token.OPERATOR);
   tokenMap.put("]", Token.OPERATOR);
   tokenMap.put("{", Token.OPERATOR);
   tokenMap.put("}", Token.OPERATOR);

   return tokenMap;
  }
  
  public void print(String text, Object... args) {
    System.out.println(String.format(text, args));
  }
  
  @Override
  public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
     // This assumes all keywords, etc. were parsed as "identifiers."
     if (tokenType==Token.IDENTIFIER) {
        int value = wordsToHighlight.get(segment, start, end);        
        if (value != -1) {
           tokenType = value;
        }
        else {
          // If there is a colon but we haven't gotten to it, then use
          // value instead of identifier, to highlight the word before the colon
          if (leftHandSide) {
            tokenType = YAML_IDENTIFIER;
          }
        }
     }
     super.addToken(segment, start, end, tokenType, startOffset);
  }

  @Override
  public Token getTokenList(Segment text, int startTokenType, int startOffset) {
    
     resetTokenList();
     
     char[] array = text.array;
     int offset = text.offset;
     int count = text.count;
     int end = offset + count;
     
     leftHandSide = true;
     
     // Token starting offsets are always of the form:
     // 'startOffset + (currentTokenStart-offset)', but since startOffset and
     // offset are constant, tokens' starting positions become:
     // 'newStartOffset+currentTokenStart'.
     int newStartOffset = startOffset - offset;

     currentTokenStart = offset;
     currentTokenType  = startTokenType;
     boolean separatorEncountered = false;

     for (int i=offset; i<end; i++) {
        if (separatorEncountered) {
          leftHandSide = false;
        }
        char c = array[i];
        
        if (c == ':') {
          separatorEncountered = true;
        }
        
        switch (currentTokenType) {

           case Token.NULL:

              currentTokenStart = i;   // Starting a new token here.

              switch (c) {

                 case ' ':
                 case '\t':
                    currentTokenType = Token.WHITESPACE;
                    break;

                 case '"':
                    currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                    break;

                 case '#':
                    currentTokenType = Token.COMMENT_EOL;
                    break;

                 default:
                    if (RSyntaxUtilities.isDigit(c)) {
                       currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                       break;
                    }
                    else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
                       currentTokenType = Token.IDENTIFIER;
                       break;
                    }

                    // Anything not currently handled - mark as an identifier
                    currentTokenType = Token.IDENTIFIER;
                    break;

              } // End of switch (c).

              break;

           case Token.WHITESPACE:

              switch (c) {
                
                case '-':
                  separatorEncountered = true;
                  // fall through...

                 case ' ':
                 case '\t':
                    break;   // Still whitespace.

                 case '"':
                    addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                    currentTokenStart = i;
                    currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                    break;

                 case '#':
                    addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                    currentTokenStart = i;
                    currentTokenType = Token.COMMENT_EOL;
                    break;

                 default:   // Add the whitespace token and start anew.

                    addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                    currentTokenStart = i;

                    if (RSyntaxUtilities.isDigit(c)) {
                       currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                       break;
                    }
                    else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
                       currentTokenType = Token.IDENTIFIER;
                       break;
                    }

                    // Anything not currently handled - mark as identifier
                    currentTokenType = Token.IDENTIFIER;

              } // End of switch (c).

              break;

           default: // Should never happen
           case Token.IDENTIFIER:
           //case Token.VARIABLE:
              switch (c) {
                
                 case ':':
                   if (leftHandSide) {
                      addToken(text, currentTokenStart,i-1, currentTokenType, newStartOffset+currentTokenStart);
                      currentTokenStart = i;
                      currentTokenType = Token.OPERATOR;
                      break;
                    }

                 case ' ':
                 case '\t':
                    addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                    currentTokenStart = i;
                    currentTokenType = Token.WHITESPACE;
                    break;

                 case '"':
                    addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                    currentTokenStart = i;
                    currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                    break;

                 default:
                    if (RSyntaxUtilities.isLetterOrDigit(c) || c=='/' || c=='_') {
                       break;   // Still an identifier of some type.
                    }
                    // Otherwise, we're still an identifier (?).

              } // End of switch (c).

              break;

           case Token.LITERAL_NUMBER_DECIMAL_INT:

              switch (c) {

                 case ' ':
                 case '\t':
                    addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                    currentTokenStart = i;
                    currentTokenType = Token.WHITESPACE;
                    break;

                 case '"':
                    addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                    currentTokenStart = i;
                    currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                    break;
                    
                 case '.':
                    // Decimal point found, so change to decimal/float
                   currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                   break;

                 default:

                    if (RSyntaxUtilities.isDigit(c)) {
                       break;   // Still a literal number.
                    }

                    // Otherwise, remember this was a number and start over.
                    //addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                    addToken(text, currentTokenStart,i-1, currentTokenType, newStartOffset+currentTokenStart);
                    i--;
                    currentTokenType = Token.NULL;

              } // End of switch (c).

              break;

           case Token.COMMENT_EOL:
              i = end - 1;
              addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
              // We need to set token type to null so at the bottom we don't add one more token.
              currentTokenType = Token.NULL;
              break;

           case Token.LITERAL_STRING_DOUBLE_QUOTE:
              if (c=='"') {
                 addToken(text, currentTokenStart,i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset+currentTokenStart);
                 currentTokenType = Token.NULL;
              }
              break;

        } // End of switch (currentTokenType).

     } // End of for (int i=offset; i<end; i++).

     switch (currentTokenType) {

        // Remember what token type to begin the next line with.
        case Token.LITERAL_STRING_DOUBLE_QUOTE:
           addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
           break;

        // Do nothing if everything was okay.
        case Token.NULL:
           addNullToken();
           break;

        // All other token types don't continue to the next line...
        default:
           addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
           addNullToken();

     }

     // Return the first token in our linked list.
     return firstToken;

  }
  
}

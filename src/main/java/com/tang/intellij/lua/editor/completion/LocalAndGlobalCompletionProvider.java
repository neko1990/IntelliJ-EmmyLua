/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.editor.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import com.tang.intellij.lua.Constants;
import com.tang.intellij.lua.highlighting.LuaSyntaxHighlighter;
import com.tang.intellij.lua.lang.LuaIcons;
import com.tang.intellij.lua.psi.*;
import com.tang.intellij.lua.search.SearchContext;
import com.tang.intellij.lua.stubs.index.LuaGlobalFuncIndex;
import com.tang.intellij.lua.stubs.index.LuaGlobalVarIndex;
import org.jetbrains.annotations.NotNull;

/**
 * suggest local/global vars and functions
 * Created by TangZX on 2017/4/11.
 */
public class LocalAndGlobalCompletionProvider extends CompletionProvider<CompletionParameters> {

    private static final int LOCAL_VAR = 1;
    private static final int LOCAL_FUN = 2;
    private static final int GLOBAL_VAR = 4;
    private static final int GLOBAL_FUN = 8;
    private static final int KEY_WORDS = 16;
    static final int ALL = LOCAL_VAR|LOCAL_FUN|GLOBAL_VAR|GLOBAL_FUN|KEY_WORDS;
    static final int VARS = LOCAL_VAR|GLOBAL_VAR;
    private int mask;

    private static final TokenSet KEYWORD_TOKENS = TokenSet.create(
            LuaTypes.AND,
            LuaTypes.BREAK,
            LuaTypes.DO,
            LuaTypes.ELSE,
            //LuaTypes.ELSEIF,
            LuaTypes.END,
            //LuaTypes.FOR,
            LuaTypes.FUNCTION,
            //LuaTypes.IF,
            LuaTypes.IN,
            LuaTypes.LOCAL,
            LuaTypes.NOT,
            LuaTypes.OR,
            LuaTypes.REPEAT,
            LuaTypes.RETURN,
            LuaTypes.THEN,
            LuaTypes.UNTIL,
            LuaTypes.WHILE,

            //Lua5.3
            LuaTypes.GOTO,
            LuaTypes.DOUBLE_COLON
    );

    LocalAndGlobalCompletionProvider(int mask) {
        this.mask = mask;
    }

    private boolean has(int flag) {
        return (mask & flag) == flag;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        CompletionSession session = completionParameters.getEditor().getUserData(CompletionSession.KEY);
        assert session != null;

        //local
        PsiElement cur = completionParameters.getPosition();
        if (has(LOCAL_VAR)) {
            LuaPsiTreeUtil.walkUpLocalNameDef(cur, nameDef -> {
                String name = nameDef.getText();
                if (completionResultSet.getPrefixMatcher().prefixMatches(name)) {
                    session.addWord(name);

                    LuaTypeGuessableLookupElement elementBuilder = new LuaTypeGuessableLookupElement(name, nameDef, false, LuaIcons.LOCAL_VAR);
                    completionResultSet.addElement(elementBuilder);
                }
                return true;
            });
        }
        if (has(LOCAL_FUN)) {
            LuaPsiTreeUtil.walkUpLocalFuncDef(cur, localFuncDef -> {
                String name = localFuncDef.getName();
                if (name != null && completionResultSet.getPrefixMatcher().prefixMatches(name)) {
                    session.addWord(name);
                    LuaPsiImplUtil.processOptional(localFuncDef.getParams(), (signature, mask) -> {
                        LocalFunctionLookupElement elementBuilder = new LocalFunctionLookupElement(name, signature, localFuncDef);
                        elementBuilder.setHandler(new FuncInsertHandler(localFuncDef).withMask(mask));
                        completionResultSet.addElement(elementBuilder);
                    });
                }
                return true;
            });
        }

        //global functions
        Project project = cur.getProject();
        if (has(GLOBAL_FUN)) {
            SearchContext context = new SearchContext(project);
            LuaGlobalFuncIndex.getInstance().processAllKeys(project, name -> {
                if (completionResultSet.getPrefixMatcher().prefixMatches(name)) {
                    LuaGlobalFuncDef globalFuncDef = LuaGlobalFuncIndex.find(name, context);
                    if (globalFuncDef != null) {
                        session.addWord(name);

                        LuaPsiImplUtil.processOptional(globalFuncDef.getParams(), (signature, mask) -> {
                            GlobalFunctionLookupElement elementBuilder = new GlobalFunctionLookupElement(name, signature, globalFuncDef);
                            elementBuilder.setHandler(new GlobalFuncInsertHandler(name, project).withMask(mask));
                            completionResultSet.addElement(elementBuilder);
                        });
                    }
                }
                return true;
            });
        }
        //global fields
        if (has(GLOBAL_VAR)) {
            SearchContext context = new SearchContext(project);
            LuaGlobalVarIndex.getInstance().processAllKeys(project, name -> {
                if (completionResultSet.getPrefixMatcher().prefixMatches(name)) {
                    LuaGlobalVar globalVar = LuaGlobalVarIndex.find(name, context);
                    if (globalVar != null) {
                        session.addWord(name);
                        LuaTypeGuessableLookupElement elementBuilder = new LuaTypeGuessableLookupElement(name, globalVar, false, LuaIcons.GLOBAL_FIELD);
                        completionResultSet.addElement(elementBuilder);
                    }
                }
                return true;
            });
        }
        //key words
        if (has(KEY_WORDS)) {
            TokenSet keywords = TokenSet.orSet(KEYWORD_TOKENS, LuaSyntaxHighlighter.PRIMITIVE_TYPE_SET);
            for (IElementType keyWordToken : keywords.getTypes()) {
                session.addWord(keyWordToken.toString());

                completionResultSet.addElement(LookupElementBuilder.create(keyWordToken)
                        .withInsertHandler(new KeywordInsertHandler(keyWordToken))
                );
            }
            completionResultSet.addElement(LookupElementBuilder.create(Constants.WORD_SELF));
        }
    }
}
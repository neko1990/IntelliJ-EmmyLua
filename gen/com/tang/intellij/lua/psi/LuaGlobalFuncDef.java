// This is a generated file. Not intended for manual editing.
package com.tang.intellij.lua.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.tang.intellij.lua.stubs.LuaGlobalFuncStub;
import com.intellij.navigation.ItemPresentation;
import com.tang.intellij.lua.comment.psi.api.LuaComment;
import com.tang.intellij.lua.lang.type.LuaTypeSet;
import com.tang.intellij.lua.search.SearchContext;

public interface LuaGlobalFuncDef extends LuaFuncBodyOwner, LuaDeclaration, LuaStatement, PsiNameIdentifierOwner, StubBasedPsiElement<LuaGlobalFuncStub> {

  @Nullable
  LuaFuncBody getFuncBody();

  @Nullable
  PsiElement getId();

  LuaComment getComment();

  ItemPresentation getPresentation();

  List<LuaParamNameDef> getParamNameDefList();

  PsiElement getNameIdentifier();

  PsiElement setName(String name);

  String getName();

  int getTextOffset();

  String toString();

  LuaTypeSet guessReturnTypeSet(SearchContext searchContext);

  @NotNull
  LuaParamInfo[] getParams();

}

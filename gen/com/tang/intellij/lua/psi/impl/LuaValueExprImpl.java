// This is a generated file. Not intended for manual editing.
package com.tang.intellij.lua.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.tang.intellij.lua.psi.LuaTypes.*;
import com.tang.intellij.lua.psi.*;

public class LuaValueExprImpl extends LuaExprImpl implements LuaValueExpr {

  public LuaValueExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LuaVisitor visitor) {
    visitor.visitValueExpr(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LuaVisitor) accept((LuaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LuaClosureFuncDef getClosureFuncDef() {
    return PsiTreeUtil.getChildOfType(this, LuaClosureFuncDef.class);
  }

  @Override
  @Nullable
  public LuaTableConstructor getTableConstructor() {
    return PsiTreeUtil.getChildOfType(this, LuaTableConstructor.class);
  }

  @Override
  @Nullable
  public LuaVar getVar() {
    return PsiTreeUtil.getChildOfType(this, LuaVar.class);
  }

}

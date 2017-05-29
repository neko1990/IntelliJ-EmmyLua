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

package com.tang.intellij.lua.debugger.remote;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 *
 * Created by tangzx on 2016/12/31.
 */
public class LuaMobStackFrame extends XStackFrame {

    private LuaMobDebuggerEvaluator evaluator = new LuaMobDebuggerEvaluator();
    private String functionName;
    private XSourcePosition position;
    private XValueChildrenList values = new XValueChildrenList();
    private XValueChildrenList up_values = new XValueChildrenList();

    public LuaMobStackFrame(String functionName, XSourcePosition position) {
        this.functionName = functionName;
        this.position = position;
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return evaluator;
    }

    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        return position;
    }

    public void addLocalValue(XNamedValue namedValue) {
        values.add(namedValue);
    }

    public void addUpValue(XNamedValue namedValue) {
        up_values.add(namedValue);
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        final ArrayList<XValueGroup> group = Lists.newArrayList();
        group.add(new XValueGroup("upvalue") {
            @Override
            public void computeChildren(@NotNull XCompositeNode node) {
                node.addChildren(up_values, true);
            }
        });
        group.add(new XValueGroup("locals") {
            @Override
            public void computeChildren(@NotNull XCompositeNode node) {
                node.addChildren(values, true);
            }
            @Override
            public boolean isAutoExpand() {
                return true;
            }
        });
        node.addChildren(XValueChildrenList.topGroups( group ), true);
    }

    public void customizePresentation(@NotNull ColoredTextContainer component) {
        XSourcePosition position = this.getSourcePosition();
        String info = functionName;
        String positionInfo = "unknown";
        if(position != null) {
            positionInfo = position.getFile().getName() + ":" + (position.getLine() + 1);
        }

        if (functionName != null)
            info = String.format("%s (%s)", functionName, positionInfo);
        component.append(info, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.setIcon(AllIcons.Debugger.StackFrame);
    }
}

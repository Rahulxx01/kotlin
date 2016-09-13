/*
 * Copyright 2010-2016 JetBrains s.r.o.
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

package org.jetbrains.kotlin.psi2ir

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi2ir.generators.GeneratorContext
import org.jetbrains.kotlin.psi2ir.generators.ModuleGenerator
import org.jetbrains.kotlin.psi2ir.transformations.insertImplicitCasts
import org.jetbrains.kotlin.resolve.BindingContext

class Psi2IrTranslator(val configuration: Psi2IrConfiguration) {
    fun generateModule(moduleDescriptor: ModuleDescriptor, ktFiles: List<KtFile>, bindingContext: BindingContext): IrModuleFragment {
        val context = GeneratorContext(configuration, moduleDescriptor, bindingContext)
        val irModule = ModuleGenerator(context).generateModuleFragment(ktFiles)
        postprocess(irModule.descriptor.builtIns, irModule)
        return irModule
    }

    fun generateSingleFileFragment(moduleDescriptor: ModuleDescriptor, ktFile: KtFile, bindingContext: BindingContext): IrModuleFragment {
        val context = GeneratorContext(configuration, moduleDescriptor, bindingContext)
        val irFileFragment = ModuleGenerator(context).generateSingleFileFragment(ktFile)
        postprocess(moduleDescriptor.builtIns, irFileFragment)
        return irFileFragment
    }

    private fun postprocess(builtIns: KotlinBuiltIns, irElement: IrElement) {
        insertImplicitCasts(builtIns, irElement)
    }
}

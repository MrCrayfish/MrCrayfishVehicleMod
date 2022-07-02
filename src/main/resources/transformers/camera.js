var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

function getFirstMatchingMethodNode(method, opcode, name, desc) {
    for (var i = 0; i < method.instructions.size(); i++) {
        var node = method.instructions.get(i);
        if(node.getOpcode() === opcode && node.name.equals(ASMAPI.mapMethod(name)) && node.desc.equals(desc)) {
            return node;
        }
    }
    return null;
}

function initializeCoreMod() {
    return {
        'camera': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.GameRenderer',
                'methodName': 'func_228378_a_',
                'methodDesc': '(FJLcom/mojang/blaze3d/matrix/MatrixStack;)V'
            },
            'transformer': function(method) {
                var node = getFirstMatchingMethodNode(method, Opcodes.INVOKEVIRTUAL, "func_216778_f", "()F");
                if(node !== null) {
                    var target = node.getNext().getNext().getNext().getNext();
                    method.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/vehicle/client/handler/CameraHandler", "setupVehicleCamera", "(Lcom/mojang/blaze3d/matrix/MatrixStack;)V"));
                    method.instructions.insert(target, new VarInsnNode(Opcodes.ALOAD, 4));
                }
                return method;
            }
        },
        'shader_camera': {
            'target': {
                'type': 'METHOD',
                'class': 'net.optifine.shaders.ShadersRender',
                'methodName': 'updateActiveRenderInfo',
                'methodDesc': '(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/Minecraft;F)V'
            },
            'transformer': function(method) {
                var node = getFirstMatchingMethodNode(method, Opcodes.INVOKEVIRTUAL, "func_216772_a", "(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V");
                if(node !== null) {
                    method.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/vehicle/client/handler/CameraHandler", "setupShaderCamera", "(Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V"));
                    method.instructions.insert(node, new VarInsnNode(Opcodes.FLOAD, 2));
                    method.instructions.insert(node, new VarInsnNode(Opcodes.ALOAD, 0));
                }
                return method;
            }
        },
        'view_offset': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.Entity',
                'methodName': 'func_195049_a',
                'methodDesc': '(DD)V'
            },
            'transformer': function(method) {
                var target = method.instructions.get(0);
                method.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/vehicle/client/handler/CameraHandler", "onPlayerTurn", "(DD)V"));
                method.instructions.insert(target, new VarInsnNode(Opcodes.DLOAD, 3));
                method.instructions.insert(target, new VarInsnNode(Opcodes.DLOAD, 1));
                return method;
            }
        }
    };
}
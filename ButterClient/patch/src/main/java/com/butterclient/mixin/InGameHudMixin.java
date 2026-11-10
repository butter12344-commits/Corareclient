package com.butterclient.mixin;
import com.butterclient.config.ButterConfig;
import com.butterclient.module.CpsCounter;
import com.butterclient.module.HitVisuals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
@Mixin(InGameHud.class)
public class InGameHudMixin {
 private static final int GLASS=0x40171126, EDGE=0x66D9B7FF;
 @Inject(method="render",at=@At("TAIL"))
 private void butter$renderHud(DrawContext d,RenderTickCounter tickCounter,CallbackInfo ci){
  ButterConfig.Data cfg=ButterConfig.data; MinecraftClient c=MinecraftClient.getInstance();
  float flash=HitVisuals.getFlashProgress(c);\n  HitVisuals.render(c,d);
  if(flash>0){int a=Math.min(42,(int)(flash*42));d.fill(0,0,d.getScaledWindowWidth(),d.getScaledWindowHeight(),(a<<24)|0x00A855FF);int cx=d.getScaledWindowWidth()/2,cy=d.getScaledWindowHeight()/2;d.fill(cx-7,cy,cx+8,cy+1,0xFFDA9CFF);d.fill(cx,cy-7,cx+1,cy+8,0xFFDA9CFF);}
  if(!cfg.hudEnabled||c.player==null||c.world==null||c.getDebugHud().shouldShowDebugHud())return;
  List<String> lines=new ArrayList<>();
  if(cfg.showWatermark)lines.add("§d§lBUTTER CLIENT §8/ §7v1.1");
  if(cfg.showInGameWelcome)lines.add("§7Welcome, §f"+(c.getSession()!=null?c.getSession().getUsername():"Player"));
  if(cfg.showFps)lines.add("§dFPS §f"+c.getCurrentFps());
  if(cfg.showCoordinates)lines.add(String.format("§dXYZ §f%d §7/ §f%d §7/ §f%d",(int)c.player.getX(),(int)c.player.getY(),(int)c.player.getZ()));
  if(cfg.showCps)lines.add("§dCPS §f"+CpsCounter.getCps());
  if(cfg.showPing&&c.getNetworkHandler()!=null){var e=c.getNetworkHandler().getPlayerListEntry(c.player.getUuid());if(e!=null)lines.add("§dPING §f"+e.getLatency()+" ms");}
  if(cfg.showDirection)lines.add("§dFACING §f"+c.player.getHorizontalFacing().asString().toUpperCase());
  if(cfg.showSprintStatus)lines.add("§dSPRINT §f"+(c.player.isSprinting()?"ON":"OFF"));
  int x=Math.max(4,cfg.hudX),y=Math.max(4,cfg.hudY),lh=cfg.compactHud?9:11,p=cfg.compactHud?4:6,mw=0;
  for(String s:lines)mw=Math.max(mw,c.textRenderer.getWidth(s));
  if(!lines.isEmpty()){int w=Math.max(100,mw+p*2+3),h=lines.size()*lh+p*2;panel(d,x-p,y-p,x+w-p,y+h-p);int yy=y;for(String s:lines){d.drawTextWithShadow(c.textRenderer,Text.literal(s),x,yy,0xFFFFFFFF);yy+=lh;}}
  int right=d.getScaledWindowWidth()-12,top=12;
  if(cfg.showKeystrokes){keys(d,c,right-104,top);top+=66;}
  if(cfg.showArmor)armor(d,c,right-132,top);
 }
 private static void panel(DrawContext d,int l,int t,int r,int b){d.fill(l,t,r,b,GLASS);d.fill(l,t,l+2,b,0xB58F5BFF);d.fill(l+2,t,r-2,t+1,EDGE);d.fill(l+2,b-1,r-2,b,0x339E7BCE);}
 private static void keys(DrawContext d,MinecraftClient c,int x,int y){
  panel(d,x,y,x+104,y+61);
  key(d,c,x+39,y+5,"W",c.options.forwardKey.isPressed());key(d,c,x+16,y+27,"A",c.options.leftKey.isPressed());key(d,c,x+39,y+27,"S",c.options.backKey.isPressed());key(d,c,x+62,y+27,"D",c.options.rightKey.isPressed());
  key(d,c,x+16,y+49,"LMB",mouse(c,0));key(d,c,x+55,y+49,"RMB",mouse(c,1));
  boolean sprint=c.options.sprintKey.isPressed()||c.player.isSprinting();d.drawTextWithShadow(c.textRenderer,Text.literal("SPRINT"),x+77,y+9,sprint?0xFFE4C8FF:0xFF9A8EA8);d.fill(x+77,y+23,x+96,y+25,sprint?0xFFD5B4FF:0x665D536A);
 }
 private static void key(DrawContext d,MinecraftClient c,int x,int y,String s,boolean down){int w=s.length()>1?34:20;d.fill(x,y,x+w,y+18,down?0xAA8052B7:0x554D405E);d.fill(x,y,x+w,y+1,down?0xFFD9B7FF:0x668F7D9F);d.drawTextWithShadow(c.textRenderer,Text.literal(s),x+(w-c.textRenderer.getWidth(s))/2,y+5,down?0xFFFFFFFF:0xFFD0C6DB);}
 private static boolean mouse(MinecraftClient c,int b){return GLFW.glfwGetMouseButton(c.getWindow().getHandle(),b)==GLFW.GLFW_PRESS;}
 private static void armor(DrawContext d,MinecraftClient c,int x,int y){
  panel(d,x,y,x+132,y+47);EquipmentSlot[] slots={EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
  for(int i=0;i<slots.length;i++){ItemStack s=c.player.getEquippedStack(slots[i]);int ix=x+7+i*31,iy=y+5;if(!s.isEmpty()){d.drawItem(s,ix,iy);if(s.isDamageable()){int max=s.getMaxDamage(),remaining=Math.max(0,max-s.getDamage());float ratio=max<=0?1f:(float)remaining/max;int color=ratio>.5f?0xFF8FE3B0:ratio>.2f?0xFFFFD27D:0xFFFF829B;d.fill(ix,y+29,ix+18,y+32,0xAA241D2D);d.fill(ix,y+29,ix+Math.max(1,Math.round(18*ratio)),y+32,color);}}else{d.fill(ix+3,iy+3,ix+15,iy+15,0x443E344A);d.drawTextWithShadow(c.textRenderer,Text.literal("-"),ix+7,iy+6,0xFF8E8499);}}
  d.drawTextWithShadow(c.textRenderer,Text.literal("ARMOR"),x+7,y+35,0xFFDCC8FF);
 }
}
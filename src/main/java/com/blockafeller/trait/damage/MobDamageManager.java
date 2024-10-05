package com.blockafeller.trait.damage;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobDamageManager {

    // Nested Map to store mob damage based on type and difficulty
    private static final Map<String, Map<String, Float>> mobDamageValues = new HashMap<>();

    public static void register() {
        initializeDamageValues(); // Populate the damage values from provided data

        // Register the attack entity event callback
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof LivingEntity) {
                if (hand == Hand.MAIN_HAND && player instanceof PlayerExtension && ((PlayerExtension) player).isInhabiting()) {
                    String mobType = ((PlayerExtension) player).getInhabitedMobType().toString();
                    String difficulty = getDifficultyLevel(world.getDifficulty());

                    // Get the damage value based on mob type and difficulty
                    float damage = getMobDamage(mobType, difficulty);

                    // Modify player's attack damage attribute based on the value
                    modifyPlayerDamage((PlayerEntity) player, damage);
                }
            }
            return ActionResult.PASS; // Allow other mods to process this event
        });
    }

    /**
     * Initializes the mob damage values based on type and difficulty.
     */
    private static void initializeDamageValues() {
            // Bee
            addMobDamage("minecraft:bee", 2.0f, 2.0f, 3.0f);

            // Blaze (melee)
            addMobDamage("minecraft:blaze_melee", 4.0f, 6.0f, 9.0f);

            // Blaze fireball
            addMobDamage("minecraft:blaze_fireball", 9.0f, 9.0f, 9.0f);

            // Cave Spider
            addMobDamage("minecraft:cave_spider", 2.0f, 2.0f, 3.0f);

            // Chicken Jockey
            addMobDamage("minecraft:chicken_jockey", 2.5f, 3.0f, 4.5f);

            // Dolphin
            addMobDamage("minecraft:dolphin", 2.5f, 3.0f, 4.5f);

            // Drowned
            addMobDamage("minecraft:drowned", 2.5f, 3.0f, 4.5f);

            // Drowned trident (Ranged)
            addMobDamage("minecraft:drowned_trident_ranged", 8.0f, 8.0f, 8.0f);

            // Drowned trident (Melee)
            addMobDamage("minecraft:drowned_trident_melee", 5.0f, 9.0f, 12.0f);

            // Elder Guardian (Laser)
            addMobDamage("minecraft:elder_guardian_laser", 5.0f, 8.0f, 12.0f);

            // Elder Guardian (Spikes)
            addMobDamage("minecraft:elder_guardian_spikes", 2.0f, 3.0f, 3.0f);

            // Ender Dragon (Melee)
            addMobDamage("minecraft:ender_dragon_melee", 6.0f, 10.0f, 15.0f);

            // Ender Dragon (Wings)
            addMobDamage("minecraft:ender_dragon_wings", 3.0f, 5.0f, 7.0f);

            // Enderman
            addMobDamage("minecraft:enderman", 4.5f, 7.0f, 10.5f);

            // Endermite
            addMobDamage("minecraft:endermite", 2.0f, 3.0f, 3.0f);

            // Evoker Fangs
            addMobDamage("minecraft:evoker_fangs", 6.0f, 6.0f, 6.0f);

            // Ghast fireball (Impact)
            addMobDamage("minecraft:ghast_fireball_impact", 6.0f, 6.0f, 6.0f);

            // Ghast fireball (Explosion)
            addMobDamage("minecraft:ghast_fireball_explosion", 7.0f, 12.0f, 22.5f);

            // Giant
            addMobDamage("minecraft:giant", 26.0f, 50.0f, 75.0f);

            // Goat
            addMobDamage("minecraft:goat", 1.0f, 2.0f, 3.0f);

            // Guardian (Laser)
            addMobDamage("minecraft:guardian_laser", 4.0f, 6.0f, 9.0f);

            // Guardian (Spikes)
            addMobDamage("minecraft:guardian_spikes", 2.0f, 3.0f, 3.0f);

            // Hoglin
            addMobDamage("minecraft:hoglin", 2.5f, 3.0f, 4.5f);

            // Husk
            addMobDamage("minecraft:husk", 2.5f, 3.0f, 4.5f);

            // Illusioner Arrow
            addMobDamage("minecraft:illusioner_arrow", 2.0f, 3.0f, 5.0f);

            // Iron Golem
            addMobDamage("minecraft:iron_golem", 4.75f, 7.5f, 11.25f);

            // Killer Bunny
            addMobDamage("minecraft:killer_bunny", 5.0f, 8.0f, 12.0f);

            // Llama Spit
            addMobDamage("minecraft:llama_spit", 1.0f, 1.0f, 1.0f);

            // Magma Cube (big)
            addMobDamage("minecraft:magma_cube_big", 4.0f, 6.0f, 9.0f);

            // Magma Cube (medium)
            addMobDamage("minecraft:magma_cube_medium", 3.0f, 4.0f, 6.0f);

            // Magma Cube (small)
            addMobDamage("minecraft:magma_cube_small", 2.5f, 3.0f, 4.5f);

            // Panda
            addMobDamage("minecraft:panda", 4.0f, 6.0f, 9.0f);

            // Phantom
            addMobDamage("minecraft:phantom", 2.0f, 3.0f, 4.0f);

            // Piglin (Melee with Sword)
            addMobDamage("minecraft:piglin_sword", 5.0f, 8.0f, 12.0f);

            // Piglin (Melee without Sword)
            addMobDamage("minecraft:piglin_no_sword", 3.5f, 5.0f, 7.5f);

            // Piglin Brute
            addMobDamage("minecraft:piglin_brute", 7.5f, 13.0f, 19.5f);

            // Polar Bear
            addMobDamage("minecraft:polar_bear", 4.0f, 6.0f, 9.0f);

            // Ravager (Melee)
            addMobDamage("minecraft:ravager_melee", 7.0f, 12.0f, 18.0f);

            // Ravager (Roar)
            addMobDamage("minecraft:ravager_roar", 4.0f, 6.0f, 9.0f);

            // Shulker Bullet
            addMobDamage("minecraft:shulker_bullet", 4.0f, 4.0f, 4.0f);

            // Silverfish
            addMobDamage("minecraft:silverfish", 1.0f, 1.0f, 1.0f);

            // Skeleton (Melee)
            addMobDamage("minecraft:skeleton_melee", 2.0f, 3.0f, 3.0f);

            // Slime (big)
            addMobDamage("minecraft:slime_big", 3.0f, 4.0f, 6.0f);

            // Slime (medium)
            addMobDamage("minecraft:slime_medium", 2.0f, 3.0f, 3.0f);

            // Spider
            addMobDamage("minecraft:spider", 2.0f, 3.0f, 3.0f);

            // Stray (Melee)
            addMobDamage("minecraft:stray_melee", 2.0f, 3.0f, 3.0f);

            // Vex
            addMobDamage("minecraft:vex", 5.5f, 9.0f, 13.5f);

            // Vindicator
            addMobDamage("minecraft:vindicator", 7.5f, 13.0f, 19.5f);

            // Warden (Melee)
            addMobDamage("minecraft:warden_melee", 16.0f, 30.0f, 45.0f);

            // Warden (Ranged)
            addMobDamage("minecraft:warden_ranged", 6.0f, 10.0f, 15.0f);

            // Wither Skeleton
            addMobDamage("minecraft:wither_skeleton", 5.0f, 8.0f, 12.0f);

            // Wolf
            addMobDamage("minecraft:wolf", 3.0f, 4.0f, 6.0f);

            // Zoglin
            addMobDamage("minecraft:zoglin", 2.5f, 3.0f, 4.5f);

            // Zombie
            addMobDamage("minecraft:zombie", 2.5f, 3.0f, 4.5f);

            // Zombified Piglin
            addMobDamage("minecraft:zombified_piglin", 5.0f, 8.0f, 12.0f);

            // Zombie Villager
            addMobDamage("minecraft:zombie_villager", 2.5f, 3.0f, 4.5f);
    }

    /**
     * Adds a new damage value entry for a mob type and its difficulty levels.
     */
    private static void addMobDamage(String mobType, float easy, float normal, float hard) {
        Map<String, Float> difficultyMap = new HashMap<>();
        difficultyMap.put("easy", easy);
        difficultyMap.put("normal", normal);
        difficultyMap.put("hard", hard);
        mobDamageValues.put(mobType, difficultyMap);
    }

    /**
     * Modifies the player's attack damage attribute to match the mob's base damage.
     */
    private static void modifyPlayerDamage(PlayerEntity player, float damage) {
        UUID DAMAGE_MODIFIER_UUID = UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");

        // Retrieve the player's attack damage attribute
        var attributeInstance = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        // Check if the modifier is already present, and remove it if necessary
        if (attributeInstance.getModifier(DAMAGE_MODIFIER_UUID) != null) {
            attributeInstance.removeModifier(DAMAGE_MODIFIER_UUID);
        }

        // Create a new modifier with the new damage value
        EntityAttributeModifier modifier = new EntityAttributeModifier(
                DAMAGE_MODIFIER_UUID, "Custom Damage Modifier", damage - 1.0f, EntityAttributeModifier.Operation.ADDITION);

        // Add the modified attribute
        attributeInstance.addPersistentModifier(modifier);
    }

    /**
     * Returns the damage value for a given mob type and difficulty.
     */
    private static  float getMobDamage(String mobType, String difficulty) {
        return mobDamageValues.getOrDefault(mobType, new HashMap<>()).getOrDefault(difficulty, 1.0f);
    }

    /**
     * Converts the Minecraft difficulty enum to a string.
     */
    private static String getDifficultyLevel(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return "easy";
            case NORMAL: return "normal";
            case HARD: return "hard";
            default: return "normal";
        }
    }
}

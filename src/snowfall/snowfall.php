<?php
/*
Plugin Name: Snowfall Effect
Description: Adds a beautiful snowfall effect to your WordPress site.
Version: 1.1
Author: esc
*/

// Enqueue the CSS and JavaScript for the snowfall effect
function christmas_snowfall_enqueue_scripts(): void
{
    wp_enqueue_style('snowfall-style', plugin_dir_url(__FILE__) . 'css/snowfall.css');
    wp_enqueue_script('snowfall-script', plugin_dir_url(__FILE__) . 'js/snowfall.js', array(), '1.0', true);
}
add_action('wp_enqueue_scripts', 'christmas_snowfall_enqueue_scripts');

// Add the snowfall container to the footer
function christmas_snowfall_footer(): void
{
    echo '<div id="snowfall-container"></div>';
}
add_action('wp_footer', 'christmas_snowfall_footer');


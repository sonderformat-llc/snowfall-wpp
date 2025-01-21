<?php
/*
Plugin Name: Snowfall Effect
Description: Adds a beautiful snowfall effect to your WordPress site.
Version: 1.1
Author: esc
*/

// Plugin Updater
require_once __DIR__ . 'class-updater-checker.php'; // Use your path to file

use wpp\snowfall\Updater_Checker; // Use your namespace

$github_username = 'escii'; // Use your gitbub username
$github_repository = 'snowfall-wpp'; // Use your repository name
$plugin_basename = plugin_basename( __FILE__ ); // Check note below
$plugin_current_version = '1.0.0'; // Use the current version of the plugin

$updater = new Updater_Checker(
    $github_username,
    $github_repository,
    $plugin_basename,
    $plugin_current_version
);
$updater->set_hooks();


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


package com.venuenext.venuenextsdkdemo.analytics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.venuenext.vncore.protocol.AnalyticsInterface
import com.venuenext.vncore.types.Event
import com.venuenext.vncore.types.Param

/**
 * VNAnalyticsInterface
 * VenueNext
 *
 * Created on 3/26/20.
 * Copyright © 2020 VenueNext Inc. All rights reserved.
 */

private const val className = "com.venuenext.venuenextsdkdemo.analytics.VNAnalyticsInterface"

class VNAnalyticsInterface(context: Context): AnalyticsInterface {

    init {
        initialize(context)
    }

    /**
     * Get class name.
     *
     * @return the className String.
     */
    override fun className(): String? {
        return className
    }

    /**
     * Initialize analytics framework.
     *
     * @param context the context Object.
     */
    override fun initialize(context: Context) {
        // Perform any necessary initialization here
    }

    /**
     * Initialize analytics framework.
     *
     * @param context the context Object.
     * @param application the application Object.
     */
    override fun initialize(context: Context, application: Application) {

    }

    /**
     * Register push notifications (inbox, messaging, etc.)
     *
     * @param token the FCM token.
     */
    override fun registerPush(token: String) {

    }

    /**
     * Handle intent routing for analytic push notifications (inbox, messaging, etc.)
     *
     * @param application the application Object.
     */
    override fun handlePushNotificationIntent(activity: Activity, intent: Intent) {

    }

    /**
     * Handle push notification display (inbox, messaging, etc.)
     *
     * @param context the context Object.
     * @param intent the intent Object.
     * @param bundle the bundled data.
     * @return boolean indicating whether or not the intent was handled.
     */
    override fun onHandleIntent(context: Context, intent: Intent?, bundle: Bundle?): Boolean {
        return false
    }

    /**
     * Tracks screen view event via analytics framework.
     *
     * @param activity the activity object.
     * @param screenName the screen name string.
     */
    override fun trackScreenView(activity: Activity, screenName: String) {
        // Register here for screen views
    }

    /**
     * Tracks event via analytics framework.
     *
     * @param eventName the action string.
     * @param attributes the bundle object.
     */
    override fun trackEvent(eventName: String, attributes: Bundle) {

    }

    /**
     * Tracks event via analytics framework.
     *
     * @param eventType the event type.
     * @param attributes the bundle object.
     */
    override fun trackEvent(event: Event, attributes: Bundle) {
        // Register here to get events
    }

    /**
     * Track userId via analytics framework.
     *
     * @param userId the userId string.
     */
    override fun trackUserId(userId: String?) {
        // Register here to get userId - Make sure to register prior to SDK initialization
    }

    /**
     * Track user property via analytics framework.
     *
     * @param name the name string.
     * @param value the value string.
     */
    override fun trackUserProperty(name: String, value: String?) {
        // Register here to get user properties
    }

    /**
     * Track menu item selection via analytics framework.
     *
     * @param itemId the itemId string.
     * @param itemName the itemName string.
     * @param itemCategory the itemCategory string.
     * @param variant the variant string.
     * @param price the price double value.
     */
    override fun trackMenuItemSelection(itemId: String, itemName: String, itemCategory: String, variant: String, price: Double) {
        val product = Bundle()
        product.putString(Param.ITEM_ID.toString(), itemId)
        product.putString(Param.ITEM_NAME.toString(), itemName)
        product.putString(Param.ITEM_CATEGORY.toString(), itemCategory)
        product.putString(Param.ITEM_VARIANT.toString(), variant)
        product.putDouble(Param.PRICE.toString(), price)
        product.putString(Param.CURRENCY.toString(), "USD")

        val ecommerceBundle = Bundle()
        ecommerceBundle.putBundle("items", product)

        trackEvent(Event.SELECT_CONTENT, ecommerceBundle)
    }

    /**
     * Track add to cart via analytics framework.
     *
     * @param itemId the itemId string.
     * @param itemName the itemName string.
     * @param itemCategory the itemCategory string.
     * @param variant the variant string.
     * @param price the price double value.
     * @param quantity the quantity int value.
     */
    override fun trackAddToCart(itemId: String, itemName: String, itemCategory: String, variant: String, price: Double, quantity: Long) {
        val product = Bundle()
        product.putString(Param.ITEM_ID.toString(), itemId)
        product.putString(Param.ITEM_NAME.toString(), itemName)
        product.putString(Param.ITEM_CATEGORY.toString(), itemCategory)
        product.putString(Param.ITEM_VARIANT.toString(), variant)
        product.putDouble(Param.PRICE.toString(), price)
        product.putString(Param.CURRENCY.toString(), "USD")
        product.putLong(Param.QUANTITY.toString(), quantity)

        val ecommerceBundle = Bundle()
        ecommerceBundle.putBundle("items", product)

        trackEvent(Event.ADD_TO_CART, ecommerceBundle)
    }

    /**
     * Track remove from cart via analytics framework.
     *
     * @param itemId the itemId string.
     * @param itemName the itemName string.
     * @param itemCategory the itemCategory string.
     * @param variant the variant string.
     * @param price the price double value.
     * @param quantity the quantity int value.
     */
    override fun trackRemoveFromCart(itemId: String, itemName: String, itemCategory: String, variant: String, price: Double, quantity: Long) {
        val product = Bundle()
        product.putString(Param.ITEM_ID.toString(), itemId)
        product.putString(Param.ITEM_NAME.toString(), itemName)
        product.putString(Param.ITEM_CATEGORY.toString(), itemCategory)
        product.putString(Param.ITEM_VARIANT.toString(), variant)
        product.putDouble(Param.PRICE.toString(), price)
        product.putString(Param.CURRENCY.toString(), "USD")
        product.putLong(Param.QUANTITY.toString(), quantity)

        val ecommerceBundle = Bundle()
        ecommerceBundle.putBundle("items", product)

        trackEvent(Event.REMOVE_FROM_CART, ecommerceBundle)
    }

    /**
     * Track adds payment type via analytics framework.
     *
     * @param paymentType the paymentType string.
     * @param cardType the cardType string.
     */
    override fun trackAddsPaymentType(paymentType: String, cardType: String) {
        val ecommerceBundle = Bundle()
        ecommerceBundle.putString(Param.CHECKOUT_OPTION.toString(), paymentType)
        ecommerceBundle.putString(Param.CHECKOUT_OPTION.toString(), cardType)

        trackEvent(Event.SET_CHECKOUT_OPTION, ecommerceBundle)
    }

    /**
     * Track begin checkout via analytics framework.
     *
     * @param items the items Bundle list.
     */
    override fun trackBeginCheckout(items: ArrayList<Bundle>) {
        val ecommerceBundle = Bundle()
        ecommerceBundle.putParcelableArrayList("items", items)

        trackEvent(Event.BEGIN_CHECKOUT, ecommerceBundle)
    }

    /**
     * Track checkout progress via analytics framework.
     *
     * @param items the items Bundle list.
     * @param orderState the order state string.
     */
    override fun trackCheckoutProgress(items: ArrayList<Bundle>, orderState: String) {
        val ecommerceBundle = Bundle()
        ecommerceBundle.putParcelableArrayList("items", items)
        ecommerceBundle.putString(Param.CHECKOUT_OPTION.toString(), orderState)

        trackEvent(Event.CHECKOUT_PROGRESS, ecommerceBundle)
    }

    /**
     * Track purchase via analytics framework.
     *
     * @param items the items Bundle list.
     * @param transactionId the variant string.
     * @param affiliation the variant string.
     * @param value the price double value.
     * @param tax the price double value.
     * @param shipping the price double value.
     */
    override fun trackPurchase(items: ArrayList<Bundle>, transactionId: String, affiliation: String, value: Double, tax: Double, shipping: Double) {
        val ecommerceBundle = Bundle()
        ecommerceBundle.putParcelableArrayList("items", items)

        ecommerceBundle.putString(Param.TRANSACTION_ID.toString(), transactionId)
        ecommerceBundle.putString(Param.AFFILIATION.toString(), affiliation)
        ecommerceBundle.putDouble(Param.VALUE.toString(), value)
        ecommerceBundle.putDouble(Param.TAX.toString(), tax)
        ecommerceBundle.putDouble(Param.SHIPPING.toString(), shipping)
        ecommerceBundle.putString(Param.CURRENCY.toString(), "USD")

        trackEvent(Event.ECOMMERCE_PURCHASE, ecommerceBundle)
    }
}